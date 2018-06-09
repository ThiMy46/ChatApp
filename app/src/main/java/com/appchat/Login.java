package com.appchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    static ProgressDialog myprogress;
    TextView moveRegister;
    private FirebaseAuth mAuth;
    EditText Email;
    EditText Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //
        mAuth = FirebaseAuth.getInstance();

        //Ánh xạ
        Email = (EditText)findViewById (R.id.input_email);
        Password = (EditText)findViewById (R.id.input_password);

        //link đến Log In từ link TextView
        moveRegister=(TextView)findViewById(R.id.link_register);
        SpannableString spannableString = new SpannableString(getString(R.string.move_register));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity((new Intent(getApplicationContext(), Register.class)).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        };
        spannableString.setSpan(clickableSpan, spannableString.length() - 15,
                spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        moveRegister.setText(spannableString, TextView.BufferType.SPANNABLE);
        moveRegister.setMovementMethod(LinkMovementMethod.getInstance());

        //END
    }

    //Login vào trang chủ
    public void LoginClick(View view) {
        myprogress=ProgressDialog.show(Login.this,"Đăng Nhập","Đợi tí...");
        //Get Text User & Pass
        String user = Email.getText ().toString ();
        String pass = Password.getText ().toString ();

        //Xử lý
        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if ( task.isSuccessful ())
                        {

                            Toast.makeText (Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show ();
                            Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(main_intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText (Login.this, "Lỗi", Toast.LENGTH_SHORT).show ();
                            myprogress.dismiss();
                        }

                    }
                });
    }
}

