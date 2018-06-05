package com.appchat.model;

import java.util.Date;

public class Message {
    String usersID;
    String message;
    long timestamp;

    public Message(String usersID, String message, long timestamp) {
        this.usersID = usersID;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    public String getUsersID() {
        return usersID;
    }

    public void setUsersID(String usersID) {
        this.usersID = usersID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
