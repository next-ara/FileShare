package com.next.module.fileshare.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.next.module.fileshare.WebService;

import java.util.ArrayList;

/**
 * ClassName:分享工具类
 *
 * @author Afton
 * @time 2023/12/5
 * @auditor
 */
public class ShareTool {

    public static final int SDK_VERSION = 1;

    private static ShareTool instance;

    //分享监听接口
    public interface OnShareListener {

        /**
         * 开启
         */
        void onShareOpen();

        /**
         * 关闭
         */
        void onShareClose();
    }

    //是否开启服务
    private boolean isRunning = false;

    //服务绑定对象
    private WebService.ShareBinder shareBinder;

    //昵称
    private String nickName = Build.MODEL;

    //服务连接对象
    private ServiceConnection serviceConnection;

    public static ShareTool getInstance() {
        if (instance == null) {
            instance = new ShareTool();
        }

        return instance;
    }

    /**
     * 启动分享文件
     *
     * @param context         上下文
     * @param fileInfoObjList 文件信息对象列表
     * @param port            端口号
     * @param onShareListener 分享监听接口
     */
    public void startShareFile(Context context, ArrayList<FileInfo> fileInfoObjList, int port, OnShareListener onShareListener) {
        this.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service != null) {
                    ShareTool.this.shareBinder = (WebService.ShareBinder) service;
                    //打开分享文件服务
                    ShareTool.this.shareBinder.getService().openShareFileServer(fileInfoObjList, port);

                    ShareTool.this.isRunning = true;
                    //发送开启监听
                    ShareTool.this.sendOpenListener(onShareListener);
                } else {
                    ShareTool.this.isRunning = false;
                    //发送关闭监听
                    ShareTool.this.sendCloseListener(onShareListener);
                    ShareTool.this.init();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ShareTool.this.isRunning = false;
                //发送关闭监听
                ShareTool.this.sendCloseListener(onShareListener);
                ShareTool.this.init();
            }
        };

        Intent intent = new Intent(context, WebService.class);
        context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 启动分享文本
     *
     * @param context         上下文
     * @param content         文本内容
     * @param port            端口号
     * @param onShareListener 分享监听接口
     */
    public void startShareText(Context context, String content, int port, OnShareListener onShareListener) {
        this.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service != null) {
                    ShareTool.this.shareBinder = (WebService.ShareBinder) service;
                    //打开分享文件服务
                    ShareTool.this.shareBinder.getService().openShareTextServer(content, port);

                    ShareTool.this.isRunning = true;
                    //发送开启监听
                    ShareTool.this.sendOpenListener(onShareListener);
                } else {
                    ShareTool.this.isRunning = false;
                    //发送关闭监听
                    ShareTool.this.sendCloseListener(onShareListener);
                    ShareTool.this.init();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ShareTool.this.isRunning = false;
                //发送关闭监听
                ShareTool.this.sendCloseListener(onShareListener);
                ShareTool.this.init();
            }
        };

        Intent intent = new Intent(context, WebService.class);
        context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 停止服务
     *
     * @param context 上下文
     */
    public void stop(Context context) {
        if (!this.isRunning) {
            return;
        }

        Intent intent = new Intent(context, WebService.class);
        context.stopService(intent);

        this.isRunning = false;
        this.init();
    }

    /**
     * 设置昵称
     *
     * @param nickName 昵称
     * @return 文件分享配置对象
     */
    public ShareTool setNickName(String nickName) {
        this.nickName = nickName;

        return this;
    }

    /**
     * 初始化
     */
    private void init() {
        this.serviceConnection = null;
        this.shareBinder = null;
        this.isRunning = false;
    }

    /**
     * 发送开启监听
     *
     * @param onShareListener 分享监听接口
     */
    private void sendOpenListener(OnShareListener onShareListener) {
        if (onShareListener != null) {
            onShareListener.onShareOpen();
        }
    }

    /**
     * 发送关闭监听
     *
     * @param onShareListener 分享监听接口
     */
    private void sendCloseListener(OnShareListener onShareListener) {
        if (onShareListener != null) {
            onShareListener.onShareClose();
        }
    }

    public String getNickName() {
        return nickName;
    }

    public boolean isRunning() {
        return isRunning;
    }
}