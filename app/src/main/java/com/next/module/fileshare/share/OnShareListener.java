package com.next.module.fileshare.share;

/**
 * ClassName:分享监听接口
 *
 * @author Afton
 * @time 2024/5/15
 * @auditor
 */
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