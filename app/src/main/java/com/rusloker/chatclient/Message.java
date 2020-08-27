package com.rusloker.chatclient;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    public final String author;
    public final long date;
    public final String content;

    public Message(String author, long date, String content) {
        this.author = author;
        this.date = date;
        this.content = content;
    }

    public Date getLocalizedDate() {
        return new Date(date);
    }
}
