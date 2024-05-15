package com.next.module.fileshare;

/**
 * ClassName:分享配置类
 *
 * @author Afton
 * @time 2024/5/15
 * @auditor
 */
public class ShareConfig {

    public static final int SDK_VERSION = 1;

    private static ShareConfig instance;

    //昵称
    private String nickName = "Unknown";

    public static ShareConfig getInstance() {
        if (instance == null) {
            instance = new ShareConfig();
        }

        return instance;
    }

    /**
     * 设置昵称
     *
     * @param nickName 昵称
     * @return 分享配置对象
     */
    public ShareConfig setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    /**
     * 获取昵称
     *
     * @return 昵称
     */
    public String getNickName() {
        return this.nickName;
    }
}