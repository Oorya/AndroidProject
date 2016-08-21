package com.example.android.finalproject.AuthActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.finalproject.TicketActivities.CreateTicket;
import com.example.android.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class Register extends AppCompatActivity {

    EditText txtUser, txtPass1, txtPass2;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setPointer();
        auth=FirebaseAuth.getInstance();
    }

    private void setPointer() {
        txtUser=(EditText)findViewById(R.id.user);
        txtPass1=(EditText)findViewById(R.id.pass1);
        txtPass2=(EditText)findViewById(R.id.pass2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private String getUUID()
    {
        //create a unique UUID
        UUID idOne = UUID.randomUUID();
        //returning the UUID
        return idOne.toString();
    }

    public void btnRegister(View v)
    {
        final String email=txtUser.getText().toString();
        final String userPass=txtPass1.getText().toString();
        final String userPass2 = txtPass2.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPass)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPass.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email,userPass)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Register.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Register.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(Register.this, CreateTicket.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    public void btnRegister1(View view) {
        //getting the info from the activity

        final String userName=txtUser.getText().toString();
        final String userPass=txtPass1.getText().toString();
        final String userPass2 = txtPass2.getText().toString();
        if(userName.equals("")||userPass.equals("")||userPass2.equals(""))
        {
            Toast.makeText(Register.this, "Please type...", Toast.LENGTH_SHORT).show();
            return;
        }
        //creating new instance of the project
        //Users user=new Users(userName,userPass,false);
        //calling inside method from the class to save the data
        //Client client = new Client(userName,userPass,"email","Client","Address","phone");
        //client.saveUser();
        txtUser.setText("");
        txtPass1.setText("");
        txtPass2.setText("");
    }

    public void btnCancel(View view) {
        finish();
    }
}
