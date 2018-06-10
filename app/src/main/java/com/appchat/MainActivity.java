package com.appchat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appchat.adapter.CustomRecyclerMainAdapter;
import com.appchat.model.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private String AuthID="";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView mAvatar;
    private TextView mTextName;

    private RecyclerView mRVlistFriendsChat;
    //lưu list friend chat
    private List<Chats> listChats;
    public static List<String> friendID;
    //lưu lại groupID của mỗi dòng list friend chat
    Map<Integer,String> mapUserGroup=new HashMap<>();

    private CustomRecyclerMainAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    int i=0;
    //Hiện hộp thoại khi đăng xuất
    AlertDialog.Builder myDialog;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Login.myprogress.dismiss();
        listChats=new ArrayList<>();
        friendID=new ArrayList<>();

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
        mAvatar=(ImageView)findViewById(R.id.activity_main_imv_avatar);
        mTextName=(TextView)findViewById(R.id.activity_main_tv_user_name);

        // If the size of views will not change as the data changes.
        mRVlistFriendsChat.setHasFixedSize(true);
        // Setting the LayoutManager.
        layoutManager = new LinearLayoutManager(this);
        mRVlistFriendsChat.setLayoutManager(layoutManager);

        //set adapter
        mAdapter=new CustomRecyclerMainAdapter(listChats);

        mRVlistFriendsChat.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        //bắt sự kiện click trên mỗi item trên list
        mAdapter.setOnItemClickedListener(new CustomRecyclerMainAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(String username, int index) {
                Intent message_intent = new Intent(getApplicationContext(), MessageList.class);
                //gửi thông tin groupID sang Message thông qua vị trí click hiện tại
                message_intent.putExtra("GroupID",mapUserGroup.get(index));
                message_intent.putExtra("class","main");
                message_intent.putExtra("UserName",username);
                startActivity(message_intent);
                finish();
                //Log.d("Group",mapUserGroup.get(Integer.parseInt(index)));
            }
        });
        getGroupOfUser();

        //get Avatar & username
        GetAvatarAndName(AuthID);

    }
    //lấy và add vào list group những group mà user tham gia\
    public void getGroupOfUser(){

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //lấy tất cả key con
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    String key = child.getKey();
                    //kiểm tra nếu key là UserID hiện tại
                    if(key.equals(AuthID))
                    {
                        //add thông tin group vào list
                        getGroupChatFromFirebaseUser(dataSnapshot.getKey());
                        getInfoFriendChat(dataSnapshot.getKey());
                        //lưu lại vị trí và name group tương ứng
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

        //lấy thông tin về tin nhắn cuối cùng
        databaseReference.child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //lấy thông tin lastmessage và time của group tương tứng
                    if (dataSnapshot.getKey().equals(group)) {
                        Chats model = dataSnapshot.getValue(Chats.class);
                        listChats.add(model);
                    }
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
    public void getInfoFriendChat(final String group)
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //lấy thông tin về tài khoản bạn bè
        databaseReference.child("members/"+group).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //lấy thông tin user từ group
                if (!dataSnapshot.getKey().equals(AuthID)) {
                    friendID.add(dataSnapshot.getKey());
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
                Log.d("Error","Unable to get last Chat: " + databaseError.getMessage());
            }
        });
    }

    //
    //Lấy Value Avatar và UserName của UserID
    public void GetAvatarAndName(String ID){
        FirebaseDatabase.getInstance().getReference().child("users/"+ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTextName.setText( dataSnapshot.child("name").getValue(String.class));
                Picasso.with(getBaseContext()).load(dataSnapshot.child("urlAvatar").getValue(String.class).toString ()).into(mAvatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                Intent search_intent = new Intent(getApplicationContext(), SearchUser.class);
                startActivity(search_intent);
                finish();
                return true;
            case R.id.about:
                About();
                Toast.makeText(this, "About button selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.help:
                Help();
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
        myDialog = new AlertDialog.Builder(this);
        myDialog.setTitle("App Chat");
        myDialog.setMessage("Bạn có chắc muốn đăng xuất???");
        myDialog.setIcon(R.mipmap.ic_launcher);
        myDialog.setPositiveButton("Đăng Xuất",new DialogInterface.OnClickListener(){   //setPositiveButton, nút hiển thị vị trí đầu bên trái
            @Override
            public void onClick(DialogInterface dialog,int which){
                FirebaseAuth.getInstance().signOut();
                Intent logout_intent = new Intent(getApplicationContext(), Login.class);
                startActivity(logout_intent);
                MainActivity.this.finish(); // đóng Activity => Thoát hoàn toàn ứng dụng
                dialog.cancel(); // đóng Dialog, Activity tiếp tục hoạt động
            }
        });
        myDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //hiển thị Dialog
        AlertDialog alertExample = myDialog.create();
        alertExample.show();
    }

    //Hiển thị danh sách bạn bè
    public void listFriendClick(View view) {
        Intent listfriend_intent = new Intent(getApplicationContext(), ListFriend.class);
        startActivity(listfriend_intent);
        finish();
    }

    //sang trang đổi pass
    public void ChangePassClick(View view) {
        Intent changepass_intent = new Intent(getApplicationContext(), ChangePassword.class);
        startActivity(changepass_intent);
    }

    //Hiển thị thông tin about and help
    public void About()
    {
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setTitle("ABOUT");
        aboutDialog.setMessage("\tĐỒ ÁN MÔN LẬP TRÌNH DI ĐỘNG\n\n" +
                "Tên đề tài: Xây dựng ứng dụng chat\n" +
                "Thành viên thực hiện:\n" +
                "\t1. Đào Thị Mỹ-15110252\n" +
                "\t2. Phạm Lương Mỹ-15110253\n\n" +
                "Version1-Finish:10/06/2018\n\n" +
                "Chi tiết xem thêm tại link: 'https://docs.google.com/document/d/1qFlfaKQibKrJIZCtvgL5fcXFaLQaE88LENf7Z4RRLRM/edit'"
                );
        aboutDialog.setPositiveButton("Cancel",new DialogInterface.OnClickListener(){   //setPositiveButton, nút hiển thị vị trí đầu bên trái
            @Override
            public void onClick(DialogInterface dialog,int which){
                dialog.cancel(); // đóng Dialog, Activity tiếp tục hoạt động
            }
        });
        //hiển thị Dialog
        AlertDialog alert = aboutDialog.create();
        alert.show();
    }
    //Hiển thị Help
    public void Help(){
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setTitle("HELP");
        aboutDialog.setMessage("\tAPP CHAT tương tự như ZALO CHAT\n\n" +
                "cần giúp đỡ hãy liên hệ theo địa chỉ mail:\n" +
                "daothimy46@gmail.com"
        );
        aboutDialog.setPositiveButton("Cancel",new DialogInterface.OnClickListener(){   //setPositiveButton, nút hiển thị vị trí đầu bên trái
            @Override
            public void onClick(DialogInterface dialog,int which){
                dialog.cancel(); // đóng Dialog, Activity tiếp tục hoạt động
            }
        });
        //hiển thị Dialog
        AlertDialog alert = aboutDialog.create();
        alert.show();
    }
}
