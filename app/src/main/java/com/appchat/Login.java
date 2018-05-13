package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //Login vào trang chủ
    public void LoginClick(View view) {
        Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main_intent);
    }
}
