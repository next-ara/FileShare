package com.next.module.fileshare.get;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.next.module.fileshare.share.ShareInfo;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ClassName:获取分享文件工具类
 *
 * @author Afton
 * @time 2024/3/27
 * @auditor
 */
public class GetShareFileTool {

    //获取分享信息监听接口
    public interface OnShareInfoListener {

        /**
         * 获取成功
         *
         * @param shareInfo 分享信息对象
         */
        void onSuccess(ShareInfo shareInfo);

        /**
         * 获取失败
         */
        void onFail();
    }

    /**
     * 获取分享信息
     *
     * @param ip                  IP地址
     * @param port                端口号
     * @param timeOut             超时时间
     * @param onShareInfoListener 获取分享信息监听接口
     */
    public static void getShareInfo(String ip, int port, long timeOut, OnShareInfoListener onShareInfoListener) {
        new Thread(() -> {
            //获取分享链接
            String shareUrl = getShareUrl(ip, port) + "/info";
            //获取主线程
            Handler handler = new Handler(Looper.getMainLooper());

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(shareUrl)
                        .build();
                Response response = client.newCall(request).execute();
                String reponseData = response.body().string();
                //解析数据
                ShareInfo shareInfo = new Gson().fromJson(reponseData, ShareInfo.class);

                handler.post(() -> {
                    //获取成功
                    onShareInfoListener.onSuccess(shareInfo);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    //获取失败
                    onShareInfoListener.onFail();
                });
            }
        }).start();
    }

    /**
     * 获取分享链接
     *
     * @param ip   IP地址
     * @param port 端口号
     * @return 分享链接
     */
    public static String getShareUrl(String ip, int port) {
        //IP:端口
        return "http://" + ip + ":" + port;
    }
}