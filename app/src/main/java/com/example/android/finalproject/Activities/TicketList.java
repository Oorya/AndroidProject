package com.example.android.finalproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.android.finalproject.Adapter.TicketListAdapter;
import com.example.android.finalproject.R;
import com.example.android.finalproject.Utility.UtlFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TicketList extends AppCompatActivity {

    private ListView listTicket;
    public static TicketListAdapter listAdapter;
    private EditText stat;
    private ProgressBar progressBar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_list);
        listTicket=(ListView)findViewById(R.id.list_ticket);
        stat=(EditText)findViewById(R.id.status);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        //FragmentManager fm = getSupportFragmentManager();
        //progressBar.setVisibility(View.VISIBLE);
        listAdapter = new TicketListAdapter(TicketList.this);
        listTicket.setAdapter(listAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void btnCreateTicket(View view) {
        startActivity(new Intent(this,CreateTicket.class));
    }

    public void btnChange(View view) {
        UtlFirebase.changeStatus("df7370ca-b203-4a6f-b03b-a871f705dfe6",stat.getText().toString());
    }

    public void btnLogOut(View view) {
        auth.signOut();

        // this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(TicketList.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }
}
