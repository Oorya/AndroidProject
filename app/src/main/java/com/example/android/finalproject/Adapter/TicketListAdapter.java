package com.example.android.finalproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.finalproject.TicketActivities.DetailsTicket;
import com.example.android.finalproject.AuthActivities.MainActivity;
import com.example.android.finalproject.Ticket;
import com.example.android.finalproject.Utility.UtlFirebase;
import com.example.android.finalproject.Utility.UtlImage;

import java.util.ArrayList;
import java.util.List;


public class TicketListAdapter extends BaseAdapter {

    List<Ticket> ticketList;
    String key="";
    Context context;
    FragmentTransaction ft;
    FragmentManager fm;
    ProgressDialog dialog;
    int numOfDB = 0;

    public TicketListAdapter(){}

    public TicketListAdapter(Context context)
    {
        this.context=context;
        ticketList=new ArrayList<>();
        ticketList = MainActivity.isAdmin()?UtlFirebase.getAllTicket()
                :UtlFirebase.getTicketByName("userName",MainActivity.userLogged(),"yosi");

    }

    public TicketListAdapter(Context context, String key)
    {
        this.context=context;
        this.key=key;
        ticketList=new ArrayList<>();
        //ticketList=UtlFirebase.getTicketByKey(key);
    }
    @Override
    public int getCount() {
        return ticketList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        LinearLayout mylayout=new LinearLayout(context);
        mylayout.setOrientation(LinearLayout.HORIZONTAL);
        mylayout.setBackgroundColor(Color.CYAN);

        TextView txt=new TextView(context);
        ImageView ticketImg1 = new ImageView(context);
        ImageView ticketImg2 = new ImageView(context);
        txt.setTextSize(30);
        if(key.equals("")) {
            txt.setText(ticketList.get(position).ticketName + "==" + ticketList.get(position).status);

            mylayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent(context, DetailsTicket.class);
                    detailsIntent.putExtra("TicketID", ticketList.get(position).ticketId);
                    context.startActivity(detailsIntent);
                }
            });
        }
        else
        {
            txt.setText(ticketList.get(position).ticketName+"\n"
                    +ticketList.get(position).ticketDes+"\n"
                    +ticketList.get(position).phone+"\n"
                    +ticketList.get(position).status+"\n");

            ticketImg1.setImageBitmap(ticketList.get(position).ticketImage1.equals("")? null: UtlImage.string2bitmap(ticketList.get(position).ticketImage1));
            ticketImg2.setImageBitmap(ticketList.get(position).ticketImage2.equals("")? null: UtlImage.string2bitmap(ticketList.get(position).ticketImage2));
        }


        mylayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UtlFirebase.removeTicket(ticketList.get(position).ticketId);
                return false;
            }
        });


        mylayout.addView(txt);
        mylayout.addView(ticketImg1);
        mylayout.addView(ticketImg2);
        return mylayout;
    }

}
