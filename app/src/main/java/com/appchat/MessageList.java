package com.appchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MessageList extends AppCompatActivity {

    private ListView listMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        //listMessage=(ListView)findViewById(R.id.list_chat_friend);
    }
}
