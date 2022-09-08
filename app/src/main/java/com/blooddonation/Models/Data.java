package com.blooddonation.Models;

public class Data {
    private String user;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String friend_request;
    private String uniqueKey;

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Data(String user, int icon, String body, String title, String sented, String friend_request,String uniqueKey) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.friend_request = friend_request;
        this.uniqueKey = uniqueKey;
    }

    public String getFriend_request() {
        return friend_request;
    }

    public void setFriend_request(String friend_request) {
        this.friend_request = friend_request;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
