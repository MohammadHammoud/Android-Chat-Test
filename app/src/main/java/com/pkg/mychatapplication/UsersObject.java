package com.pkg.mychatapplication;

public class UsersObject {
    private String image;
    private String PhoneNum;

    public UsersObject(String image,String PhoneNum) {
        this.image = image;
        this.PhoneNum = PhoneNum;
    }
    public UsersObject(){}

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }




    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
