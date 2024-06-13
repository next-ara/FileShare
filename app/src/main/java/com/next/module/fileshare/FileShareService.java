package com.next.module.fileshare;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.next.module.file2.File2;
import com.next.module.fileshare.share.FileInfo;
import com.next.module.fileshare.share.ShareFileInfo;
import com.next.module.fileshare.share.ShareInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ClassName:文件分享服务类
 *
 * @author Afton
 * @time 2023/12/5
 * @auditor
 */
public class FileShareService extends Service {

    //异步Http服务对象
    private CustomAsyncHttpServer server = new CustomAsyncHttpServer();

    //异步服务对象
    private AsyncServer mAsyncServer = new AsyncServer();

    //文件信息对象Map
    private HashMap<String, FileInfo> fileMap = new HashMap<>();

    //分享绑定对象
    private IBinder iBinder = new ShareBinder();

    //分享绑定对象类
    public class ShareBinder extends Binder {

        /**
         * 获取服务对象
         *
         * @return 服务对象
         */
        public FileShareService getService() {
            return FileShareService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.iBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.server != null) {
            this.server.stop();
        }
        if (this.mAsyncServer != null) {
            this.mAsyncServer.stop();
        }

        this.fileMap.clear();

        this.server = null;
        this.mAsyncServer = null;
        this.fileMap = null;
        this.iBinder = null;
    }

    /**
     * 打开分享文件服务
     *
     * @param fileInfoObjList 文件信息对象列表
     * @param port            端口号
     */
    public void openShareFileServer(ArrayList<FileInfo> fileInfoObjList, int port) {
        //文件信息对象列表转Map
        this.fileInfoObjListToMap(fileInfoObjList);
        //启动分享文件服务
        this.startShareFileServer(port);
    }

    /**
     * 关闭服务
     */
    public void closeServer() {
        this.stopSelf();
    }

    /**
     * 文件信息对象列表转Map
     *
     * @param fileInfoObjList 文件信息对象列表
     */
    private void fileInfoObjListToMap(ArrayList<FileInfo> fileInfoObjList) {
        this.fileMap.clear();
        if (fileInfoObjList == null || fileInfoObjList.isEmpty()) {
            return;
        }

        for (int i = 0; i < fileInfoObjList.size(); i++) {
            FileInfo fileInfo = fileInfoObjList.get(i);
            File2 file = fileInfo.getFile();
            if (file.exists() && file.isFile()) {
                this.fileMap.put(String.valueOf(i), fileInfo);
            }
        }
    }

    /**
     * 启动分享文件服务
     *
     * @param port 端口号
     */
    private void startShareFileServer(int port) {
        String dirPath = "share/file/";
        this.server.get(this.getApplicationContext(), "/js/.*", dirPath);
        this.server.get(this.getApplicationContext(), "/css/.*", dirPath);
        this.server.get(this.getApplicationContext(), "/image/.*", dirPath);
        this.server.get("/", (asyncHttpServerRequest, asyncHttpServerResponse) -> {
            try {
                asyncHttpServerResponse.send(this.getIndexContent(dirPath + "index.html"));
            } catch (Exception e) {
                asyncHttpServerResponse.code(500).end();
            }
        });

        //请求文件列表接口
        this.server.get("/info", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            ArrayList<ShareFileInfo> shareFileInfoList = new ArrayList<>();

            for (String index : this.fileMap.keySet()) {
                FileInfo fileInfo = this.fileMap.get(index);
                try {
                    ShareFileInfo shareFileInfo = new ShareFileInfo();
                    shareFileInfo.setIndex(index);
                    shareFileInfo.setFileName(fileInfo.getFileName());
                    shareFileInfo.setFileSize(this.formetFileSize(fileInfo.getFile().length()));
                    shareFileInfoList.add(shareFileInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ShareInfo shareInfo = new ShareInfo(ShareConfig.getInstance().getNickName(), shareFileInfoList);
            response.send(new Gson().toJson(shareInfo));
        });

        //下载文件接口
        this.server.get("/download/.*", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            String index = request.getPath().replace("/download/", "");
            try {
                index = URLDecoder.decode(index, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            FileInfo fileInfo = this.fileMap.get(index);
            File2 file = fileInfo.getFile();
            if (file.exists() && file.isFile()) {
                try {
                    response.getHeaders().add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getFileName(), "utf-8"));
                    response.sendStream(file.openInputStream(), file.length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            response.code(404).send("Not found!");
        });

        //下载App接口
        this.server.get("/app", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            //获取App安装包路径
            String sourceDir = "";

            PackageManager pm = this.getPackageManager();
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(this.getPackageName(), 0);
                sourceDir = appInfo.sourceDir;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            File file = new File(sourceDir);
            if (file != null && file.exists() && file.isFile()) {
                try {
                    response.getHeaders().add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                response.sendFile(file);
                return;
            }

            response.code(404).send("Not found!");
        });

        this.server.listen(this.mAsyncServer, port);
    }

    /**
     * 获取index.html内容
     *
     * @param filePath 文件路径
     * @return index.html内容
     * @throws IOException
     */
    private String getIndexContent(String filePath) throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(getAssets().open(filePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] tmp = new byte[10240];
            while ((len = bInputStream.read(tmp)) > 0) {
                baos.write(tmp, 0, len);
            }

            return new String(baos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 文件大小转换
     *
     * @param fileS 文件大小
     * @return 文件大小文本
     */
    private String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";

        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }

        return fileSizeString;
    }
}