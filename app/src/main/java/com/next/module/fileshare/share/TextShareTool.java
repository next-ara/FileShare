package com.next.module.fileshare.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.next.module.fileshare.TextShareService;

/**
 * ClassName:文本分享工具类
 *
 * @author Afton
 * @time 2023/12/5
 * @auditor
 */
public class TextShareTool {

    private static TextShareTool instance;

    //是否开启服务
    private boolean isRunning = false;

    //服务绑定对象
    private TextShareService.ShareBinder shareBinder;

    //服务连接对象
    private ServiceConnection serviceConnection;

    public static TextShareTool getInstance() {
        if (instance == null) {
            instance = new TextShareTool();
        }

        return instance;
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
                    TextShareTool.this.shareBinder = (TextShareService.ShareBinder) service;
                    //打开分享文件服务
                    TextShareTool.this.shareBinder.getService().openShareTextServer(content, port);

                    TextShareTool.this.isRunning = true;
                    //发送开启监听
                    TextShareTool.this.sendOpenListener(onShareListener);
                } else {
                    TextShareTool.this.isRunning = false;
                    //发送关闭监听
                    TextShareTool.this.sendCloseListener(onShareListener);
                    TextShareTool.this.init();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                TextShareTool.this.isRunning = false;
                //发送关闭监听
                TextShareTool.this.sendCloseListener(onShareListener);
                TextShareTool.this.init();
            }
        };

        Intent intent = new Intent(context, TextShareService.class);
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

        Intent intent = new Intent(context, TextShareService.class);
        context.stopService(intent);

        this.isRunning = false;
        this.init();
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

    public boolean isRunning() {
        return isRunning;
    }
}