package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appchat.adapter.CustomRecyclerSearchAdapter;
import com.appchat.model.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchUser extends AppCompatActivity {

    private EditText mSearchEdt;
    private Button mSearchBtn;
    private CustomRecyclerSearchAdapter mAdapter;
    private RecyclerView mResultList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    public List<UserInformation> listUserQuery= new ArrayList<>();
    public List<String> keyUser=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");


        mSearchEdt = (EditText) findViewById(R.id.edtSearch);
        mSearchBtn = (Button) findViewById(R.id.btnSearch);

        mResultList = (RecyclerView) findViewById(R.id.rvSearch);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        //set adapter
        mAdapter=new CustomRecyclerSearchAdapter(listUserQuery);
        mResultList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        //load lời mời kết bạn
        FriendInvitation();
        //search Button
        mSearchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                listUserQuery.clear();
                keyUser.clear();
                String searchText = mSearchEdt.getText().toString();
                firebaseUserSearch(searchText);

            }
        });
        mAdapter.setOnItemClickedListener(new CustomRecyclerSearchAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick( int index) {

                UserInformation user=listUserQuery.get(index);
                Intent user_intent=new Intent(SearchUser.this,InfoUserSearch.class);
                //gửi thông tin groupID thông qua vị trí click hiện tại
                user_intent.putExtra("name",user.getName());
                user_intent.putExtra("sex",user.getSex());
                user_intent.putExtra("sdt",user.getSdt());
                user_intent.putExtra("mail",user.getMail());
                user_intent.putExtra("urlAvatar",user.getUrlAvatar());

                //gửi thông tin UserID của user tìm đc
                user_intent.putExtra("UserID",keyUser.get(index));
                startActivity(user_intent);
                //Toast.makeText(SearchUser.this, keyUser.get(index), Toast.LENGTH_SHORT).show();
            }
        });

        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=new Intent(SearchUser.this,MainActivity.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(null);
    }
    //search theo name trong bảng user
    private void firebaseUserSearch(String searchText) {

        Toast.makeText(SearchUser.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mDatabaseReference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot puserSnapshot: dataSnapshot.getChildren()) {
                    UserInformation user= puserSnapshot.getValue(UserInformation.class);
                    listUserQuery.add(user);
                    keyUser.add(puserSnapshot.getKey());
                    //Log.d("Key",puserSnapshot.getKey());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //danh sách những user khác gửi lời mời kết bạn đến
    public void FriendInvitation()
    {
        mAuth = FirebaseAuth.getInstance();
        final String AuthID=mAuth.getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    int flag=0;
                    for (DataSnapshot dspcon : dsp.getChildren()) {
                        if(dspcon.getKey().equals(AuthID)&&!dspcon.getValue(Boolean.class))
                        {
                            flag=1;
                        }
                    }
                    if(flag==1) {
                        for (DataSnapshot dspcon : dsp.getChildren()) {
                            if (!dspcon.getKey().equals(AuthID) && dspcon.getValue(Boolean.class)) {
                                getFriendInvitation(dspcon.getKey());
                                Log.d("BBBB",dspcon.getKey()+"");
                                Log.d("BBBB",AuthID);
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
    public void getFriendInvitation(final String key){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users/"+key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation user=dataSnapshot.getValue(UserInformation.class);
                listUserQuery.add(user);
                keyUser.add(key);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
