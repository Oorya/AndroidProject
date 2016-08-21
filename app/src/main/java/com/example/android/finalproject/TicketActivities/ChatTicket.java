package com.example.android.finalproject.TicketActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.finalproject.Adapter.ChatAdapter;
import com.example.android.finalproject.AuthActivities.MainActivity;
import com.example.android.finalproject.Chat;
import com.example.android.finalproject.R;

public class ChatTicket extends AppCompatActivity {

    ListView listView ;
    EditText txtMsg;
    Context context;
    String ticketID;
    public static ChatAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_ticket);
        ticketID=getIntent().getStringExtra("TicketID");
        txtMsg=(EditText)findViewById(R.id.txtMsg);
        listView=(ListView)findViewById(R.id.list_view);
        context=this;

        chatAdapter = new ChatAdapter(this,ticketID);
        listView.setAdapter(chatAdapter);
    }

    public void btnSendMsg(View view) {
        Chat chat = new Chat(MainActivity.userLogged(),txtMsg.getText().toString(),
                ticketID);

        chat.saveChat(ticketID);
        txtMsg.setText("");
    }
}
