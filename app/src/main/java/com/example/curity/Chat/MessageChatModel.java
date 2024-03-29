package com.example.curity.Chat;

public class MessageChatModel {

    public String id;
    private String text;
    private String time;
    public int viewType;
    public String imgUrl = "";

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

    public Boolean hasImage(){
        if(imgUrl.equals(""))
            return false;

        return true;
    }
}