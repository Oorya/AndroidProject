package com.example.android.finalproject.AuthActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.finalproject.TicketActivities.TicketList;
import com.example.android.finalproject.R;
import com.example.android.finalproject.Utility.UtlFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private EditText txtUser, txtPass;
    private String passDB="",userName="";
    private boolean existUserName;
    private ProgressDialog dialog;
    private static boolean admin=false;
    private static String myName="";
    public static Context context;
    private FirebaseAuth auth;
    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUser = (EditText) findViewById(R.id.user);
        txtPass = (EditText) findViewById(R.id.pass);
        context = this;
        auth = FirebaseAuth.getInstance();
        UtlFirebase.onSendTicket();
        text=(TextView)findViewById(R.id.user1);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, auth.getCurrentUser().getEmail()+"\n"+
                        auth.getCurrentUser().getUid()+"\n"+
                        auth.getCurrentUser().getDisplayName()+"\n"+
                        auth.getCurrentUser().getProviderId()+"\n", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static Bitmap decodeSampledBitmapFromFile(String path,int reqWidth, int reqHeight)
    {
        // BEST QUALITY MATCH
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }


        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    public void btnLogin(View view)  {

        final String userName=txtUser.getText().toString().toLowerCase();
        final String password= txtPass.getText().toString();

        if(userName.equals("") || password.equals("")) {
            Toast.makeText(this, "Please type username and password", Toast.LENGTH_LONG).show();
            return;
        }

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please wait.....");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //creating an instance to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //creating a reference to the database
        final DatabaseReference myRef = database.getReference("Users");

        //adding an event listener to the Event
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            //method for data change
            public void onDataChange(DataSnapshot dataSnapshot) {
                passDB=dataSnapshot.child(userName).child("userPass").getValue()+"";
                existUserName=dataSnapshot.child(userName).getValue()==null?false:true;
                admin=Boolean.parseBoolean(dataSnapshot.child(userName).child("isAdmin").getValue()+"");

                Log.w("ADMIN",admin+"");
                dialog.dismiss();
                if(!existUserName) {
                    Toast.makeText(MainActivity.this, "Please register", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(!password.equals(passDB)) {
                    Toast.makeText(MainActivity.this, password+"  Wrong password "+passDB, Toast.LENGTH_SHORT).show();
                    return;
                }

                myName=userName;
                UtlFirebase.onStatusChanged(userLogged());

                startActivity(new Intent(MainActivity.this,TicketList.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle in error case
            }
        });

    }

    private Date getCurrentTime()
    {
        //Calendar calendar=Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd:MM:yyyy");
        //get current date time with Date()
        Date date = new Date();

        //return dateFormat.format(cal.getTime()));
        return date;
    }
    public static boolean isAdmin()
    {
        return admin;
    }

    public static String userLogged()
    {
        return myName;
    }

    public void btnReg(View view)
    {
        Toast.makeText(MainActivity.this, "REG", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void btnSignOut(View view) {
        auth.signOut();

        // this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }
}
