package com.appchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private FirebaseUser user;
    private EditText passOld, passNew, passNewAgain;
    private Button btnChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //ánh xạ
        btnChange=(Button)findViewById(R.id.btnChangePass);
        passOld=(EditText)findViewById(R.id.edtPassOld);
        passNew=(EditText)findViewById(R.id.edtPassNew);
        passNewAgain=(EditText)findViewById(R.id.edtPassNewAgain);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passNew.getText().length()<6&&passNewAgain.getText().length()<6)
                {
                    Toast.makeText(ChangePassword.this,"Độ dài MK mới phải >6 ký tự", Toast.LENGTH_LONG).show();
                }
                else if(!passNew.getText().toString().trim().equals(passNewAgain.getText().toString().trim()))
                {
                    Toast.makeText(ChangePassword.this,"Nhập lại MK mới không trùng với MK mới", Toast.LENGTH_LONG).show();
                }
                else {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    final String email = user.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, passOld.getText().toString().trim());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(passNew.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(ChangePassword.this, "Đã xảy ra lỗi. Hãy quay lại sau!",
                                                    Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(ChangePassword.this, "Thay đổi mật khẩu thành công!",
                                                    Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePassword.this, "Authentication Failed",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(null);
    }
}
