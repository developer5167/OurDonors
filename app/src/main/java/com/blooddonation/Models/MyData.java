package com.blooddonation.Models;

public class MyData {
    private static MyData myData;

    public static MyData getMyData() {
        return myData;
    }

    public static void setMyData(MyData myData) {
        MyData.myData = myData;
    }


    private String name;
    private String age;
    private String my_id;
    private String bg;
    private String img_url;
    private String location;
    private String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public MyData(String name, String age, String my_id, String bg, String img_url, String location) {
        this.name = name;
        this.age = age;
        this.my_id = my_id;
        this.bg = bg;
        this.img_url = img_url;
        this.location = location;
    }

    public MyData() {
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

    public String getMy_id() {
        return my_id;
    }

    public void setMy_id(String my_id) {
        this.my_id = my_id;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
