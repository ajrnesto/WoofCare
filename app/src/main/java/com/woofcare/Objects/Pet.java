package com.woofcare.Objects;

public class Pet {
    String uid;
    String name;
    String photoUrl;
    String fileName;
    String sex;
    long birthday;

    public Pet() {
    }

    public Pet(String uid, String name, String photoUrl, String fileName, String sex, long birthday) {
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.fileName = fileName;
        this.sex = sex;
        this.birthday = birthday;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }
}
