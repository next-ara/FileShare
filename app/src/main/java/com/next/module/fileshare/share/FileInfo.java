package com.next.module.fileshare.share;

import java.io.File;
import java.io.Serializable;

/**
 * ClassName:文件信息类
 *
 * @author Afton
 * @time 2024/5/10
 * @auditor
 */
public class FileInfo implements Serializable {

    //文件名称
    private String fileName;

    //文件路径
    private String filePath;

    //文件对象
    private File file;

    public FileInfo(File file) {
        this.file = file;
        this.filePath = file.getPath();
        this.fileName = file.getName();
    }

    public FileInfo(String filePath) {
        this.filePath = filePath;
        this.file = new File(filePath);
        this.fileName = file.getName();
    }

    public FileInfo(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.file = new File(filePath);
    }

    public FileInfo(String fileName, String filePath, File file) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public File getFile() {
        return file;
    }
}