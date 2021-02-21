package com.ugur.mychatapp.Classes;

public class UploadFile {
    private String userUUID;
    private String fileName;
    private String fileUri;
    private String fileSize;

    public UploadFile() {
    }

    public UploadFile(String userUUID, String fileName, String fileUri, String fileSize) {
        this.userUUID = userUUID;
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileSize = fileSize;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
