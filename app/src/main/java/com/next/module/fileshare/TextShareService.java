package com.next.module.fileshare;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.next.module.fileshare.share.ShareTextInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ClassName:文本分享服务类
 *
 * @author Afton
 * @time 2023/12/5
 * @auditor
 */
public class TextShareService extends Service {

    //异步Http服务对象
    private CustomAsyncHttpServer server = new CustomAsyncHttpServer();

    //异步服务对象
    private AsyncServer mAsyncServer = new AsyncServer();

    //分享绑定对象
    private final IBinder iBinder = new ShareBinder();

    //分享绑定对象类
    public class ShareBinder extends Binder {

        /**
         * 获取服务对象
         *
         * @return 服务对象
         */
        public TextShareService getService() {
            return TextShareService.this;
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
    }

    /**
     * 打开分享文本服务
     *
     * @param content 文本内容
     * @param port    端口号
     */
    public void openShareTextServer(String content, int port) {
        //启动分享文本服务
        this.startShareTextServer(content, port);
    }

    /**
     * 关闭服务
     */
    public void closeServer() {
        this.stopSelf();
    }

    /**
     * 启动分享文本服务
     *
     * @param content 文本内容
     * @param port    端口号
     */
    private void startShareTextServer(String content, int port) {
        String dirPath = "share/text/";
        this.server.get(this.getApplicationContext(), "/js/.*", dirPath);
        this.server.get(this.getApplicationContext(), "/css/.*", dirPath);
        this.server.get("/", (asyncHttpServerRequest, asyncHttpServerResponse) -> {
            try {
                asyncHttpServerResponse.send(this.getIndexContent(dirPath + "index.html"));
            } catch (Exception e) {
                asyncHttpServerResponse.code(500).end();
            }
        });

        //请求文本接口
        this.server.get("/text", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            ShareTextInfo shareTextInfo = new ShareTextInfo(ShareConfig.getInstance().getNickName(), content);
            response.send(new Gson().toJson(shareTextInfo));
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
}