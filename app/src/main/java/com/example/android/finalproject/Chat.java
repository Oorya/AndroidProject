package com.example.android.finalproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class Chat
{
    public String userName;
    public String msg;
    public String tickeId;
    public String time;

    public Chat(){}

    public Chat(String userName, String msg, String uid)
    {
        this.userName=userName;
        this.msg=msg;
        this.tickeId=uid;
        this.time=getCurrentTime();
    }

    private String getUUID()
    {
        //create a unique UUID
        UUID idOne = UUID.randomUUID();
        //returning the UUID
        return idOne.toString();
    }

    private String getCurrentTime()
    {
        //Calendar calendar=Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd:MM:yyyy");
        //get current date time with Date()
        Date date = new Date();

        //return dateFormat.format(cal.getTime()));
        return dateFormat.format(date);
    }

    public void saveChat(String reference)
    {
        //create an instance of User class
        Chat chat=new Chat(userName, msg, tickeId);

        //creating a connection to fire base
        FirebaseDatabase database= FirebaseDatabase.getInstance();

        //creating a reference to Users object
        DatabaseReference myRef=database.getReference("Chat");

        //saving the user and msg under the UUID
        myRef.child(reference).child(time).setValue(chat);
    }
}
