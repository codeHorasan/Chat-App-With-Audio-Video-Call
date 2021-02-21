package com.ugur.mychatapp.Classes;

import android.net.Uri;

public class User {
    private String uuid;
    private String name;
    private String email;
    private String id;
    private Uri imageUri;

    private static User user;

    private User() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }
}
