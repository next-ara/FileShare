package com.next.module.fileshare.share;

import com.next.module.file2.File2;

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
    private File2 file;

    public FileInfo(File2 file) {
        this.file = file;
        this.filePath = file.getPath();
        this.fileName = file.getName();
    }

    public FileInfo(String fileName, String filePath, File2 file) {
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

    public File2 getFile() {
        return file;
    }
}