package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Info_Account extends AppCompatActivity {

    TextView Name;
    TextView Sex;
    TextView Mail;
    TextView Sdt;
    ImageView avatar;
    String urlAvatar;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_info_account);

        //Ánh xạ
        Name = (TextView) findViewById (R.id.txtName);
        Sex = (TextView) findViewById (R.id.txtSex);
        Mail = (TextView) findViewById (R.id.txtMail);
        Sdt = (TextView) findViewById (R.id.txtSdt);
        avatar = (ImageView) findViewById (R.id.avatar);
        //
        DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ();
        FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();

        mDatabase.child("users/"+user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name.setText(dataSnapshot.child("name").getValue(String.class));
                String sdt=dataSnapshot.child("sdt").getValue(String.class);
                if(sdt.trim().equals("none")){
                    Sdt.setText("");
                }else
                    Sdt.setText(sdt);
                Sex.setText(dataSnapshot.child("sex").getValue(String.class));
                Mail.setText(dataSnapshot.child("mail").getValue(String.class));
                urlAvatar=dataSnapshot.child("urlAvatar").getValue(String.class);
                Picasso.with(getBaseContext()).load(urlAvatar).into (avatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void UpdateInfoClick(View view){
        Intent edit_intent = new Intent (getApplicationContext (), UpdateInfoAccount.class);
        edit_intent.putExtra("name",Name.getText());
        edit_intent.putExtra("sdt",Sdt.getText());
        edit_intent.putExtra("sex",Sex.getText());
        edit_intent.putExtra("mail",Mail.getText());
        edit_intent.putExtra("urlAvatar",urlAvatar);
        startActivity (edit_intent);
    }
}
