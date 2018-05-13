package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Info_Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__account);
    }

    public void UpdateInfoClick(View view) {
        Intent edit_intent = new Intent(getApplicationContext(), UpdateInfoAccount.class);
        startActivity(edit_intent);
    }
}
