package com.appchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPass extends AppCompatActivity {
    EditText txtMail;
    Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        //Ánh xạ
        txtMail=(EditText)findViewById (R.id.txtMail);
        btnSave=(Button)findViewById (R.id.btnSave);
        //
        btnSave.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                String mail=txtMail.getText ().toString ();
                confirm(mail);
            }
        });

        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    void confirm(String mail){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(mail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText (ResetPass.this, "Thông tin xác thực đã được gửi đi!\n Hãy kiểm tra Mail.", Toast.LENGTH_SHORT).show ();
                            Intent info_intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(info_intent);
                            finish();
                        }
                        else
                            Toast.makeText (ResetPass.this, "Mail không chính xác!", Toast.LENGTH_SHORT).show ();
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(null);
    }
}
