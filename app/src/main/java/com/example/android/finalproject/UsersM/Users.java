package com.example.android.finalproject.UsersM;

import java.util.UUID;

/**
 * Created by אוריה on 07/07/2016.
 */
public abstract class Users
{
    public String userName;  //hold userName
    public String userPass;  //hold userPass
    public String email;
    public String userId; //hold userId
    public boolean isAdmin;
    public String status;
    public String address;
    public String phone;

    public Users(){}  //empty constructor, must have

    public Users(String userName, String userPass, String email, String status, String address, String phone, String userId) {
        this.userName = userName;
        this.userPass = userPass;
        this.email = email;
        this.userId = userId;
        this.status = status;
        this.address = address;
        this.phone = phone;
        this.isAdmin = false;
    }

    private String getUUID()
    {
        //create a unique UUID
        UUID idOne = UUID.randomUUID();
        //returning the UUID
        return idOne.toString();
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}
