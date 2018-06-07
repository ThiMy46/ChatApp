package com.appchat.model;

public class UserInformation{
    public String name;
    public String sex;
    public String mail;
    public String sdt;
    public String urlAvatar;

    public UserInformation(){

    }

    public UserInformation(String name, String sex, String mail, String sdt, String urlAvatar){
        this.name = name;
        this.sex = sex;
        this.mail = mail;
        this.sdt = sdt;
        this.urlAvatar = urlAvatar;
    }

    public UserInformation(String name, String sex, String sdt){
        this.name = name;
        this.sex = sex;
        this.sdt = sdt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        if(sdt.trim().equals(""))
            this.sdt = "none";
        else
            this.sdt=sdt;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }
}

