package com.example.fimae.models;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String sender; // người gửi tin nhắn
    private String content; // nội dung tin nhắn
    private Date timestamp; // thời điểm gửi tin nhắn

    public Message(String sender, String content, Date timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public static ArrayList<Message> dummy = new ArrayList<Message>(){
        {
            new Message("John Doe", "Hello, how are you?", new Date());
                    new Message("Jane Smith", "I'm good, thanks for asking. How about you?", new Date());
                    new Message("John Doe", "I'm doing great, thanks. Are you free this weekend?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "That sounds like a great idea! What time should we meet?", new Date());
                    new Message("John Doe", "Hello, how are you?", new Date());
                    new Message("Jane Smith", "I'm good, thanks for asking. How about you?", new Date());
                    new Message("John Doe", "I'm doing great, thanks. Are you free this weekend?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "That sounds like a great idea! What time should we meet?", new Date());
                    new Message("John Doe", "Hello, how are you?", new Date());
                    new Message("Jane Smith", "I'm good, thanks for asking. How about you?", new Date());
                    new Message("John Doe", "I'm doing great, thanks. Are you free this weekend?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "That sounds like a great idea! What time should we meet?", new Date());
                    new Message("John Doe", "Hello, how are you?", new Date());
                    new Message("Jane Smith", "I'm good, thanks for asking. How about you?", new Date());
                    new Message("John Doe", "I'm doing great, thanks. Are you free this weekend?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "Yes, I am. What did you have in mind?", new Date());
                    new Message("Jane Smith", "That sounds like a great idea! What time should we meet?", new Date());
        }

    };
}
