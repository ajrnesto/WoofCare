package com.woofcare.Objects;

public class Schedule {
    String uid;
    String petUid;
    String title;
    String details;
    long timestamp;
    String category;

    public Schedule() {
    }

    public Schedule(String uid, String petUid, String title, String details, long timestamp, String category) {
        this.uid = uid;
        this.petUid = petUid;
        this.title = title;
        this.details = details;
        this.timestamp = timestamp;
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPetUid() {
        return petUid;
    }

    public void setPetUid(String petUid) {
        this.petUid = petUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
