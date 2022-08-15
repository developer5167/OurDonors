package com.blooddonation;

public class MyAccountDetails {
    private String name;
    private String age;
    private String bg;
    private String location;
    private String img_url;
    private String my_id;
    private String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public MyAccountDetails() {
    }

    public MyAccountDetails(String name, String age, String bg, String location, String img_url, String my_id) {
        this.name = name;
        this.age = age;
        this.bg = bg;
        this.location = location;
        this.img_url = img_url;
        this.my_id = my_id;
    }

    public String getMy_id() {
        return my_id;
    }

    public void setMy_id(String my_id) {
        this.my_id = my_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
