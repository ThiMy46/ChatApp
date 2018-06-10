package com.appchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

public class InfoUserSearch extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnKetBan;
    private String userGroup;
    int friend=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user_search);
        //get UserID đăng nhập hiện tại
        mAuth = FirebaseAuth.getInstance();
        final String AuthID=mAuth.getCurrentUser().getUid();
        //ánh xạ
        ImageView imgHinh=(ImageView)findViewById(R.id.imgHinh);
        final TextView tvName=(TextView)findViewById(R.id.tvName);
        TextView tvSex=(TextView)findViewById(R.id.tvSex);
        TextView tvSDT=(TextView)findViewById(R.id.tvSdt);
        TextView tvEmail=(TextView)findViewById(R.id.tvMail);
        btnKetBan=(Button)findViewById(R.id.btnAdd);

        Intent intent=getIntent();

        tvName.setText(intent.getStringExtra("name"));
        tvSex.setText(intent.getStringExtra("sex"));
        tvSDT.setText(intent.getStringExtra("sdt"));
        tvEmail.setText(intent.getStringExtra("mail"));
        Picasso.with(getBaseContext())
                .load(intent.getStringExtra("urlAvatar"))
                .into (imgHinh);
        final String IDUser=intent.getStringExtra("UserID");

        TestFriended(AuthID,IDUser);

        btnKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //thêm group vào members
                if(friend==0) {//gui ket ban
                    AddGroup(AuthID, IDUser);
                    Toast.makeText(InfoUserSearch.this, "Đã gửi lời kết bạn với " + tvName.getText(), Toast.LENGTH_SHORT).show();
                    btnKetBan.setText("Đã gửi kết bạn");
                    friend=1;
                }
                else if(friend==1){//huy kết bạn
                    CancelGroup(AuthID,IDUser);
                    btnKetBan.setText("kết bạn");
                    friend=0;
                    Toast.makeText(InfoUserSearch.this, "Đã hủy lời kết bạn với " + tvName.getText(), Toast.LENGTH_SHORT).show();
                }
                else if(friend==2){//chấp nhận kết bạn
                    AcceptInvite(AuthID,IDUser);
                    btnKetBan.setText("Bạn Bè");
                    btnKetBan.setEnabled(false);
                }
            }
        });
        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void AcceptInvite(String authID, String idUser) {
        FirebaseDatabase.getInstance().getReference().child("members/"+userGroup).child(authID)
                .setValue(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(null);
    }

    public void AddGroup(String ID1Auth,String ID2){
        Map<String,Boolean> map=new HashMap<>();
        map.put(ID1Auth,true);
        map.put(ID2,false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        userGroup = databaseReference.child("members").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("members/"+userGroup)
                .setValue(map);
    }
    public void CancelGroup(String AuthID, String IDUser){
        FirebaseDatabase.getInstance().getReference().child("members/").child(userGroup).removeValue();
    }
    public void TestFriended(final String AuthID, final String IDUser){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    int flag=0;
                    //kiểm tra key có chứa AuthID ko
                    for (DataSnapshot dspcon : dsp.getChildren()) {
                        if(dspcon.getKey().equals(AuthID) && dspcon.getValue(Boolean.class))
                        {
                            flag=1;
                        }
                        else if(dspcon.getKey().equals(AuthID) && !dspcon.getValue(Boolean.class))
                        {
                            flag=2;
                        }
                    }
                    if(flag==1) {
                        for (DataSnapshot dspcon : dsp.getChildren()) {
                            if (dspcon.getKey().equals(IDUser)&& dspcon.getValue(Boolean.class)) {
                                btnKetBan.setText("Bạn Bè");
                                btnKetBan.setEnabled(false);
                            }
                            else if(dspcon.getKey().equals(IDUser)&& !dspcon.getValue(Boolean.class))
                            {
                                btnKetBan.setEnabled(true);
                                friend=1;
                                btnKetBan.setText("Đã gửi kết bạn");
                                userGroup=dsp.getKey();
                                //Toast.makeText(InfoUserSearch.this, userGroup, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else if(flag==2){
                        for (DataSnapshot dspcon : dsp.getChildren()) {
                            if (dspcon.getKey().equals(IDUser)&& dspcon.getValue(Boolean.class)) {
                                btnKetBan.setEnabled(true);
                                friend = 2;
                                btnKetBan.setText("Chấp nhận kết bạn");
                                userGroup=dsp.getKey();
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
