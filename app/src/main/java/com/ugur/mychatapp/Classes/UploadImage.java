package com.ugur.mychatapp.Classes;

public class UploadImage {
    private String userUUID;
    private String imageUri;

    public UploadImage() {
    }

    public UploadImage(String userUUID, String imageUri) {
        this.userUUID = userUUID;
        this.imageUri = imageUri;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
