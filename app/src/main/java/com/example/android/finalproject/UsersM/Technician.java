package com.example.android.finalproject.UsersM;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by אוריה on 10/08/2016.
 */
public class Technician extends Users {

    public String calenderID;
    public String shifts;

    public Technician(){}

    public Technician(String userName, String userPass, String email, String status, String address, String phone,String userID,
                      String calenderID, String shifts) {
        super(userName, userPass, email, status, address, phone,userID);
        this.calenderID=calenderID;
        this.shifts=shifts;
    }

    public void saveUser()
    {
        //create an instance of User class
        Users technician=new Technician(userName,userPass,"email","Client","Address","phone",userId,"calederID","Shifts");

        //creating a connection to fire base
        FirebaseDatabase database= FirebaseDatabase.getInstance();

        //creating a reference to Users object
        DatabaseReference myRef=database.getReference("Technician");

        //saving the user under the UUID
        myRef.child(userName).setValue(technician);
    }
}
