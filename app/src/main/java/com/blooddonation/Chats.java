package com.blooddonation;

public class Chats {
    private String sender;
    private String receiver;
    private String msg;
    private boolean is_seen;
    private String type;

    public boolean isIs_seen() {
        return is_seen;
    }

    public void setIs_seen(boolean is_seen) {
        this.is_seen = is_seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Chats() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
