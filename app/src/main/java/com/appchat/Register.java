package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Register extends AppCompatActivity {

    EditText mEditEmail;
    EditText mEditPassword;

    TextView moveLogin;
    EditText mEditName,mEditNumber,mEditPass,mEditAgainPass;
    RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //link đến Log In từ link TextView
        moveLogin=(TextView)findViewById(R.id.link_login);
        SpannableString spannableString = new SpannableString(getString(R.string.move_login));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity((new Intent(getApplicationContext(), Login.class)).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        };
        spannableString.setSpan(clickableSpan, spannableString.length() - 17,
                spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        moveLogin.setText(spannableString, TextView.BufferType.SPANNABLE);
        moveLogin.setMovementMethod(LinkMovementMethod.getInstance());
        //END

        //Ánh xạ
        mEditName=(EditText)findViewById(R.id.mEditName);
        mEditNumber=(EditText)findViewById(R.id.mEditNumber);
        mEditPass=(EditText)findViewById(R.id.mEditPass);
        mEditAgainPass=(EditText)findViewById(R.id.mEditAgainPass);

    }

    //Đăng ký hoàn tất quay về trang LogIn
}
