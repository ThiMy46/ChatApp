package com.appchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.appchat.adapter.CusReListFriendAdapter;
import com.appchat.model.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private String AuthID="";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private RecyclerView mRVlistFriendsChat;
    //lưu list friend chat
    private List<Chats> listChats=new ArrayList<>();
    //lưu lại groupID của mỗi dòng list friend chat
    Map<Integer,String> mapUserGroup=new HashMap<>();

    private CusReListFriendAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    int i=0;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toggle navigation
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //get UserID đăng nhập hiện tại
        mAuth = FirebaseAuth.getInstance();
        AuthID=mAuth.getCurrentUser().getUid();
        //ánh xạ
        mRVlistFriendsChat=(RecyclerView) findViewById(R.id.list_chat_friend);

        // If the size of views will not change as the data changes.
        mRVlistFriendsChat.setHasFixedSize(true);
        // Setting the LayoutManager.
        layoutManager = new LinearLayoutManager(this);
        mRVlistFriendsChat.setLayoutManager(layoutManager);

        //set adapter
        mAdapter=new CusReListFriendAdapter(listChats);

        mRVlistFriendsChat.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        //bắt sự kiện click trên mỗi item trên list
        mAdapter.setOnItemClickedListener(new CusReListFriendAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(String index) {
                Toast.makeText(MainActivity.this, index, Toast.LENGTH_SHORT).show();//thay đổi lấy thông tin group chat qua list chats
                Intent message_intent = new Intent(getApplicationContext(), MessageList.class);
                //gửi thông tin groupID sang Message
                message_intent.putExtra("GroupID",mapUserGroup.get(Integer.parseInt(index)));
                startActivity(message_intent);
                //Log.d("Group",mapUserGroup.get(Integer.parseInt(index)));
            }
        });
        getGroupOfUser();

    }
    //lấy và add vào list group những group mà user tham gia\
    public void getGroupOfUser(){

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //KEY: group1
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    String key = child.getKey();
                    if(key.equals(AuthID))
                    {
                        getGroupChatFromFirebaseUser(dataSnapshot.getKey());
                        mapUserGroup.put(i++,dataSnapshot.getKey());
                    }
                }
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
                Log.d("Error","Unable to get group: " + databaseError.getMessage());
            }
        });
    }
    //lấy và tự động cập nhập dữ liệu danh sách group chat trên firebase
    public void getGroupChatFromFirebaseUser(final String group) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //for(String group : groups) {
                    if (dataSnapshot.getKey().equals(group)) {
                        Chats model = dataSnapshot.getValue(Chats.class);
                        listChats.add(model);
                    }
                //}
                mAdapter.notifyDataSetChanged();
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
                Log.d("Error","Unable to get last Chat: " + databaseError.getMessage());
            }
        });
    }

    //toggle disday menu left
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //menu right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "Search button selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                Toast.makeText(this, "About button selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.help:
                Toast.makeText(this, "Help button selected", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //chuyển sang trang Thông tin tài khoản
    public void InforAccountClick(View view) {
        Intent info_intent = new Intent(getApplicationContext(), Info_Account.class);
        startActivity(info_intent);
    }

    //Đăng xuất tài khoản khỏi hệ thống, quay về đăng nhập
    public void LogOut(View view) {
        Intent logout_intent = new Intent(getApplicationContext(), Login.class);
        startActivity(logout_intent);
    }

    //hiển thị list chat của item tương ứng
    public void linkMessageList(View view) {
        Intent info_MessageList = new Intent(getApplicationContext(), MessageList.class);
        startActivity(info_MessageList);
    }


}
