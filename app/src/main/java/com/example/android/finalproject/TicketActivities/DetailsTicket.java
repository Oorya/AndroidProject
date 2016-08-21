package com.example.android.finalproject.TicketActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.finalproject.Enums.TicketStatus;
import com.example.android.finalproject.Interfaces.FireBaseAble;
import com.example.android.finalproject.R;
import com.example.android.finalproject.Ticket;
import com.example.android.finalproject.Utility.UtlFirebase;
import com.example.android.finalproject.Utility.UtlImage;

public class DetailsTicket extends AppCompatActivity implements FireBaseAble {

    private String ticketID;
    private static TextView txt;
    private static ImageView ticketImg1, ticketImg2, fullScreen;
    private static ProgressBar progressBar;
    private Button btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_ticket);
        pointer();

        progressBar.setVisibility(View.VISIBLE);
        UtlFirebase.getTicketByKey(ticketID,this);
    }

    private void pointer()
    {
        ticketID=getIntent().getStringExtra("TicketID");
        txt=(TextView)findViewById(R.id.txtContain);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        ticketImg1=(ImageView)findViewById(R.id.img1);
        ticketImg2=(ImageView)findViewById(R.id.img2);
        fullScreen=(ImageView)findViewById(R.id.fullScreenContainer);
        btnChat=(Button)findViewById(R.id.btnChat);
        txt.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void result(final Ticket ticket) {
        progressBar.setVisibility(View.INVISIBLE);
        txt.setText( ticket.userName+"  שלום    "+"\n"
                +ticket.status+"\n"
                +ticket.product+"\n"
                +ticket.classification+"\n"
                +ticket.subClassification+"\n"
                +ticket.ticketName+"\n"
                +ticket.ticketDes+"\n"
                +ticket.phone+"\n"
        );
        if(!ticket.ticketImage1.equals("error"))
        {
            ticketImg1.setImageBitmap(UtlImage.string2bitmap(ticket.ticketImage1));

            ticketImg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fullScreen.setImageBitmap(UtlImage.string2bitmap(ticket.ticketImage1));
                    fullScreen.setVisibility(View.VISIBLE);
                }
            });
        }

        if(!ticket.ticketImage2.equals("error"))
        {
            ticketImg2.setImageBitmap(UtlImage.string2bitmap(ticket.ticketImage2));

            ticketImg2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fullScreen.setImageBitmap(UtlImage.string2bitmap(ticket.ticketImage2));
                    fullScreen.setVisibility(View.VISIBLE);
                }
            });
        }

        Log.e("IN RESULT", ticket==null?"NULL":ticket.ticketName);


        if(ticket.status.equals(TicketStatus.Approval.toString()))
        {
            btnChat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (fullScreen.getVisibility() == View.VISIBLE) {
            fullScreen.setImageBitmap(null);
            fullScreen.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void btnStartChat(View view) {
        Intent chatIntent = new Intent(this, ChatTicket.class);
        chatIntent.putExtra("TicketID",ticketID);
        startActivity(chatIntent);
        finish();
    }

    public void btnCancelTicket(View view) {
        TicketStatus ticketStatus= TicketStatus.TicketClosed;
        UtlFirebase.changeStatus(ticketID,ticketStatus.toString());
        finish();
    }
}
