package com.example.chatapp.model;

//Test git abc 1231
public class Users {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String fullName;
    private String phoneNumber;
    private String cmnd;

    public Users(String id, String username, String imageURL,String status, String fullName, String phoneNumber,String cmnd) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.fullName=fullName;
        this.phoneNumber=phoneNumber;
        this.cmnd=cmnd;
    }

    public Users()
    {}

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL){this.imageURL=imageURL;}


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }
}
