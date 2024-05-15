package com.next.module.fileshare.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.next.module.fileshare.FileShareService;

import java.util.ArrayList;

/**
 * ClassName:文件分享工具类
 *
 * @author Afton
 * @time 2023/12/5
 * @auditor
 */
public class FileShareTool {

    private static FileShareTool instance;

    //是否开启服务
    private boolean isRunning = false;

    //服务绑定对象
    private FileShareService.ShareBinder shareBinder;

    //服务连接对象
    private ServiceConnection serviceConnection;

    public static FileShareTool getInstance() {
        if (instance == null) {
            instance = new FileShareTool();
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
                    FileShareTool.this.shareBinder = (FileShareService.ShareBinder) service;
                    //打开分享文件服务
                    FileShareTool.this.shareBinder.getService().openShareFileServer(fileInfoObjList, port);

                    FileShareTool.this.isRunning = true;
                    //发送开启监听
                    FileShareTool.this.sendOpenListener(onShareListener);
                } else {
                    FileShareTool.this.isRunning = false;
                    //发送关闭监听
                    FileShareTool.this.sendCloseListener(onShareListener);
                    FileShareTool.this.init();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                FileShareTool.this.isRunning = false;
                //发送关闭监听
                FileShareTool.this.sendCloseListener(onShareListener);
                FileShareTool.this.init();
            }
        };

        Intent intent = new Intent(context, FileShareService.class);
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

        Intent intent = new Intent(context, FileShareService.class);
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