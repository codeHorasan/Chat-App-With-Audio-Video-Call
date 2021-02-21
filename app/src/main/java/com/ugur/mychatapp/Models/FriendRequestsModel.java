package com.ugur.mychatapp.Models;

import android.net.Uri;

public class FriendRequestsModel {
    private String id;
    private String name;
    private Uri imageUri;
    //Date

    public FriendRequestsModel(String id, String name, Uri imageUri) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
