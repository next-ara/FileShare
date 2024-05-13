package com.next.module.fileshare.share;

import java.util.ArrayList;

/**
 * ClassName:分享信息类
 *
 * @author Afton
 * @time 2023/12/25
 * @auditor
 */
public class ShareInfo {

    //昵称
    private String nickName = "";

    //分享文件信息对象列表
    private ArrayList<ShareFileInfo> shareFileInfoList;

    public ShareInfo(String nickName, ArrayList<ShareFileInfo> shareFileInfoList) {
        this.nickName = nickName;
        this.shareFileInfoList = shareFileInfoList;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public ArrayList<ShareFileInfo> getShareFileInfoList() {
        return shareFileInfoList;
    }

    public void setShareFileInfoList(ArrayList<ShareFileInfo> shareFileInfoList) {
        this.shareFileInfoList = shareFileInfoList;
    }
}