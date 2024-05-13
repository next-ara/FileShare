package com.next.module.fileshare.share;

/**
 * ClassName:分享文件信息类
 *
 * @author Afton
 * @time 2023/12/25
 * @auditor
 */
public class ShareFileInfo {

    //排序
    private String index;

    //文件名称
    private String fileName;

    //文件大小
    private String fileSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}