package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class InfoUserSearch extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public Button btnKetBan;
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
                AddGroup(AuthID, IDUser);
                Toast.makeText(InfoUserSearch.this, "Đã Kết Bạn với "+tvName.getText(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(null);
    }

    public void AddGroup(String ID1,String ID2){
        Map<String,Boolean> map=new HashMap<>();
        map.put(ID1,true);
        map.put(ID2,true);
        FirebaseDatabase.getInstance().getReference().child("members/")
                .push()
                .setValue(map);
    }
    public void TestFriended(final String AuthID, final String IDUser){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    int flag=0;
                    for (DataSnapshot dspcon : dsp.getChildren()) {
                        if(dspcon.getKey().equals(AuthID))
                        {
                            flag=1;
                        }
                    }
                    if(flag==1) {
                        for (DataSnapshot dspcon : dsp.getChildren()) {
                            if (dspcon.getKey().equals(IDUser)) {
                                btnKetBan.setText("Bạn Bè");
                                btnKetBan.setEnabled(false);
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
