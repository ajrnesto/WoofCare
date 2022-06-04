package com.woofcare.Objects;

public class Memory {
    String uid;
    String title;
    long timestamp;
    String journal;

    public Memory() {
    }

    public Memory(String uid, String title, long timestamp, String journal) {
        this.uid = uid;
        this.title = title;
        this.timestamp = timestamp;
        this.journal = journal;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }
}

