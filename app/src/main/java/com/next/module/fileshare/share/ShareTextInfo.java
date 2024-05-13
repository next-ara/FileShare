package com.next.module.fileshare.share;

/**
 * ClassName:分享文本信息类
 *
 * @author Afton
 * @time 2024/4/9
 * @auditor
 */
public class ShareTextInfo {

    //昵称
    private String nickName = "";

    //内容
    private String content = "";

    public ShareTextInfo(String nickName, String content) {
        this.nickName = nickName;
        this.content = content;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}