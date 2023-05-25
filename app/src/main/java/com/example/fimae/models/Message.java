package com.example.fimae.models;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String id;
    private String conversationId;
    private String idSender; // người gửi tin nhắn
    private String content; // nội dung tin nhắn
    private Date timeSent; // thời điểm gửi tin nhắn

    public Message(){}
    public Message(String sender, String content, Date timeSent) {
        this.idSender = sender;
        this.content = content;
        this.timeSent = timeSent;
    }

    public String getIdSender() {
        return idSender;
    }

    public String getContent() {
        return content;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public static ArrayList<Message> dummy = new ArrayList<Message>() {
        {
            add(new Message("John Doe", "Hello, how are you?", new Date()));
            add(new Message("Jane Smith", "I'm good, thanks for asking. How about you?", new Date()));
            add(new Message("John Doe", "I'm doing great, thanks. Are you free this weekend?", new Date()));
            add(new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date()));
            add(new Message("Jane Smith", "That sounds like a great idea! What time should we meet?", new Date()));
            // Thêm các mục tin nhắn khác vào danh sách ở đây
            add(new Message("John Doe", "Hello, how are you?", new Date()));
            add(new Message("Jane Smith", "I'm good, thanks for asking. How about you?", new Date()));
            add(new Message("John Doe", "I'm doing great, thanks. Are you free this weekend?", new Date()));
            add(new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date()));
            add(new Message("Jane Smith", "That sounds like a great idea! What time should we meet?", new Date()));
            add(new Message("John Doe", "Hello, how are you?", new Date()));
            add(new Message("Jane Smith", "I'm good, thanks for asking. How about you?", new Date()));
            add(new Message("John Doe", "I'm doing great, thanks. Are you free this weekend?", new Date()));
            add(new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date()));
            add(new Message("Jane Smith", "That sounds like a great idea! What time should we meet?", new Date()));
        }
    };

}
