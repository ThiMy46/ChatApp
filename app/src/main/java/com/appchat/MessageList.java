package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appchat.adapter.CustomRecyclerAdapter;
import com.appchat.model.Chats;
import com.appchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageList extends AppCompatActivity {
    //thay đồi khi load members lên được
    private String groupchats="";////////////////////////////group chat sửa lại
    private String AuthID="";

    private FirebaseAuth mAuth;
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private EditText editMessage;
    private Button btnSend;
    private List<Message> listMessage= new ArrayList<>();;
    CustomRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        //get UserID đăng nhập hiện tại
        mAuth = FirebaseAuth.getInstance();
        AuthID=mAuth.getCurrentUser().getUid();
        //get GroupID từ Main
        Intent intent = getIntent();
        groupchats = intent.getStringExtra("GroupID");

        //ánh xạ các phần tử
        mRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        editMessage=(EditText)findViewById(R.id.edittext_chatbox);
        //btnSend=(Button)findViewById(R.id.button_chatbox_send);

        // If the size of views will not change as the data changes.
        mRecyclerView.setHasFixedSize(true);
        // Setting the LayoutManager.
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //set adapter
        adapter=new CustomRecyclerAdapter(listMessage);

        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Lấy Data từ firebase và bắt các thay đổi dữ liệu của data
        getMessageFromFirebaseUser();
        //btnSend.setOnClickListener(this);

    }
    /*onClick of button Send*/
    public void AdđItem(View view) {
        long time=new Date().getTime();
        // get data.
        Message messageToAdd = new Message(AuthID,editMessage.getText().toString(),time);///////////////sửa lại tên người gửi
        Chats chat=new Chats(editMessage.getText().toString(),time);

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance().getReference().child("messages/"+groupchats)
                .push()
                .setValue(messageToAdd);
        FirebaseDatabase.getInstance().getReference().child("chats/"+groupchats)
                .setValue(chat);

    }

    //lấy và tự động cập nhập dữ liệu danh sách message trên firebase
    public void getMessageFromFirebaseUser() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("messages/" + groupchats).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //listMessage.clear();
                        // Result will be holded Here
                        //for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Message model=dataSnapshot.getValue(Message.class);
                            listMessage.add(model);
                            editMessage.setText("");
                        //}
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Error","Unable to get message: " + databaseError.getMessage());
                    }
                });
    }
}
