package com.appchat;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.appchat.adapter.CustomRecyclerFriendAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFriend extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String AuthID="";

    private RecyclerView mRVlistFriend;
    //lưu list friend chat
    private List<String> listFriend=new ArrayList<>();
    //lưu lại groupID của mỗi dòng list friend chat
    Map<Integer,String> mapUserGroup=new HashMap<>();

    private CustomRecyclerFriendAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friend);

        //get UserID đăng nhập hiện tại
        mAuth = FirebaseAuth.getInstance();
        AuthID=mAuth.getCurrentUser().getUid();
        //ánh xạ
        mRVlistFriend=(RecyclerView) findViewById(R.id.list_friend);

        // If the size of views will not change as the data changes.
        mRVlistFriend.setHasFixedSize(true);
        // Setting the LayoutManager.
        layoutManager = new LinearLayoutManager(this);
        mRVlistFriend.setLayoutManager(layoutManager);

        //set adapter
        mAdapter=new CustomRecyclerFriendAdapter(listFriend);

        mRVlistFriend.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        //bắt sự kiện click trên mỗi item trên list
        mAdapter.setOnItemClickedListener(new CustomRecyclerFriendAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(String username, int index) {
                Toast.makeText(ListFriend.this, mapUserGroup.get(index), Toast.LENGTH_SHORT).show();
                Intent message_intent = new Intent(getApplicationContext(), MessageList.class);
                //gửi thông tin groupID sang Message thông qua vị trí click hiện tại
                message_intent.putExtra("GroupID",mapUserGroup.get(index));
                message_intent.putExtra("class","list");
                message_intent.putExtra("UserName",username);
                startActivity(message_intent);  //
                finish();
            }
        });
        getFriendOfUser();

        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    // Button Back
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=new Intent(ListFriend.this,MainActivity.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(null);
    }
    //lấy và add vào list group những group mà user tham gia\
    public void getFriendOfUser(){

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lấy tất cả key con
                    listFriend.clear();
                    // Result will be holded Here
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        int flag=0;
                        for (DataSnapshot dspcon : dsp.getChildren()) {
                            if(dspcon.getKey().equals(AuthID))
                            {
                                flag=1;
                                Log.d("BB","Auth: "+dspcon.getValue(Boolean.class)+"");
                            }
                        }
                        if(flag==1) {
                            for (DataSnapshot dspcon : dsp.getChildren()) {
                                if (!dspcon.getKey().equals(AuthID)) {
                                    //Log.d("KEYBAN", dspcon.getKey());//////////ID BAN
                                    listFriend.add(dspcon.getKey());
                                    //Log.d("KEYCON",dsp.getKey());//////////groupID
                                    mapUserGroup.put(i++,dsp.getKey());
                                    Log.d("BB2","friend: "+dspcon.getValue(Boolean.class)+"");
                                }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
