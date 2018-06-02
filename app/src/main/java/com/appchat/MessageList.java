package com.appchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

public class MessageList extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private ListView listMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);

        //listMessage=(ListView)findViewById(R.id.list_chat_friend);
    }
}
