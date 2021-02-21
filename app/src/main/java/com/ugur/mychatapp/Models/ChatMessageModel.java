package com.ugur.mychatapp.Models;

import android.net.Uri;

import java.util.Date;

public class ChatMessageModel implements Comparable<ChatMessageModel> {
    private String message;
    private String senderId;
    private String receiverId;
    private Date date;
    private Uri fileUri;
    private Uri imageUri;
    private String fileName;
    private String fileSize;

    private String TYPE;

    //Text Message Constructor
    public ChatMessageModel(String message, String senderId, String receiverId, Date date) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.date = date;
        this.TYPE = "TEXT";
    }

    //File Message Constructor
    public ChatMessageModel(String senderId, String receiverId, Date date, Uri fileUri, String fileName, String fileSize) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.date = date;
        this.fileUri = fileUri;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.TYPE = "FILE";
    }


    //Image Message Constructor
    public ChatMessageModel(String senderId, String receiverId, Date date, Uri imageUri) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.date = date;
        this.imageUri = imageUri;
        this.TYPE = "IMAGE";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

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

    public String getTYPE() {
        return TYPE;
    }

    @Override
    public int compareTo(ChatMessageModel o) {
        if (this.date.after(o.getDate())) {
            return 1;
        } else {
            return -1;
        }
    }
}
