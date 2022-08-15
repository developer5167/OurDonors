package com.blooddonation;

public class PojoAllPosts {
    private String date;
    private String owner;
    private String postUrl;
    private String current;

    public PojoAllPosts() {
    }

    public PojoAllPosts(String date, String owner, String postUrl, String current) {
        this.date = date;
        this.owner = owner;
        this.postUrl = postUrl;
        this.current = current;
    }

    public String getDate() {
        return date;
    }

    public String getOwner() {
        return owner;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getCurrent() {
        return current;
    }
}
