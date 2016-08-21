package com.example.android.finalproject.UsersM;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by אוריה on 10/08/2016.
 */
public class Client extends Users
{
    public Client(){}

    public Client(String userName, String userPass, String email, String status, String address, String phone,String userId) {
        super(userName, userPass, email, status, address, phone, userId);
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

    public void saveUser()
    {
        //create an instance of User class
        Client client=new Client(userName,userPass,email,status,address,phone,userId);

        //creating a connection to fire base
        FirebaseDatabase database= FirebaseDatabase.getInstance();

        //creating a reference to Users object
        DatabaseReference myRef=database.getReference("Client");

        //saving the user under the UUID
        myRef.child(userId).setValue(client);
    }
}
