package com.next.module.fileshare.get;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.next.module.fileshare.share.ShareTextInfo;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ClassName:获取分享文本工具类
 *
 * @author Afton
 * @time 2024/4/9
 * @auditor
 */
public class GetShareTextTool {

    //获取分享信息监听接口
    public interface OnShareInfoListener {

        /**
         * 获取成功
         *
         * @param shareTextInfo 分享文本信息对象
         */
        void onSuccess(ShareTextInfo shareTextInfo);

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
            String shareUrl = getShareUrl(ip, port) + "/text";
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
                ShareTextInfo shareTextInfo = new Gson().fromJson(reponseData, ShareTextInfo.class);

                handler.post(() -> {
                    //获取成功
                    onShareInfoListener.onSuccess(shareTextInfo);
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