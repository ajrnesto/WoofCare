package com.woofcare.Objects;

public class Photo {
    String uid;
    String photoUrl;
    String fileName;

    public Photo() {
    }

    public Photo(String uid, String photoUrl, String fileName) {
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.fileName = fileName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
