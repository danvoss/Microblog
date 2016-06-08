package com.dvoss;

/**
 * Created by Dan on 6/6/16.
 */
public class Message {
    int msgDelete;
    String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
