package com.example.yash.nodesocketmessaging;

/**
 * Created by Yash on 03-03-2018.
 */

public class UserUtils {

    //private variables
    private String userTo, userFrom, userMessage, userTimestamp, userName, userEmail;

    // Empty constructor
    public UserUtils() {
    }

    // ALL GETTER AND SETTER METHODS FROM THIS POINT

    public String getTime() {
        return userTimestamp;
    }

    public void setTime(String time) {
        this.userTimestamp = time;
    }

    public String getMessage() {
        return userMessage;
    }

    public void setMessage(String message) {
        this.userMessage = message;
    }

    public String getName() {
        return this.userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public String getEmail() {
        return userEmail;
    }

    public void setEmail(String Email) {
        this.userEmail = Email;
    }

    public String getTo() {
        return userTo;
    }

    public void setTo(String Email) {
        this.userTo = Email;
    }

    public String getFrom() {
        return userFrom;
    }

    public void setFrom(String Email) {
        this.userFrom = Email;
    }
}
