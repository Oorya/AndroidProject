package com.example.android.finalproject;

import com.example.android.finalproject.Activities.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by אוריה on 18/07/2016.
 */
public class Ticket {
    public String userName;
    public String product;
    public String classification;
    public String subClassification;
    public String ticketName;  //hold userName
    public String ticketDes;
    public String area;
    public String address;
    public String ticketImage1;
    public String ticketImage2;
    public String phone;
    public String ticketId;
    public String status;
    public String time;
    public Date endTime;
    public String ticketNumber;
    public String calenderID;
    public String eventID;

    public Ticket(){}  //empty constructor, must have

    public Ticket(String product,String classification,String subClassification,String ticketName, String ticketDes,
                  String phone, String status, String area,String address,String ticketImage1,String ticketImage2,String ticketId)
    {
        this.userName = MainActivity.userLogged();
        this.product=product;
        this.classification=classification;
        this.subClassification=subClassification;
        this.ticketName=ticketName;
        this.ticketDes=ticketDes;
        this.area=area;
        this.address=address;
        this.ticketImage1=ticketImage1;
        this.ticketImage2=ticketImage2;
        this.phone=phone;
        this.ticketId=ticketId;
        this.status=status;
        this.time=getCurrentTime();
        this.ticketNumber="ticket number";
    }

    public void saveTicket()
    {
        //create an instance of User class
        Ticket ticket=new Ticket(product,classification,subClassification,ticketName,ticketDes, phone
                , status,area, address, ticketImage1,ticketImage2, ticketId);

        //creating a connection to fire base
        FirebaseDatabase database= FirebaseDatabase.getInstance();

        //creating a reference to Users object
        DatabaseReference myRef=database.getReference("Ticket");

        //saving the user under the UUID
        myRef.child(ticketId).setValue(ticket);
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

    private String getUUID()
    {
        //create a unique UUID
        UUID idOne = UUID.randomUUID();
        //returning the UUID
        return idOne.toString();
    }

    public String toString()
    {
        String str="";
        str+="Ticket name: "+this.ticketName+"\n";
        str+="Ticket description: "+this.ticketDes+"\n";
        str+="Phone: "+this.phone+"\n";
        str+="User name: "+this.userName+"\n";
        str+="Status: "+this.status+"\n";
        return str;
    }
}
