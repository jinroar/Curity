package com.example.curity.model;

public class MessageChatModel {

    public String id;
    private String text;
    private String time;
    private int viewType;

    //Plain message
    public MessageChatModel(String text, String time, int viewType) {
        this.text = text;
        this.time = time;
        this.viewType = viewType;
    }



    //Getter
    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public int getViewType() {
        return viewType;
    }


}