package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class Login extends AppCompatActivity {


    TextView moveRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main_intent);
    }
}
