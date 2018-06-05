package com.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Info_Account extends AppCompatActivity {

    ImageView imageView;
    String url="https://firebasestorage.googleapis.com/v0/b/chatmini-182f5.appspot.com/o/image1528126445288.png?alt=media&token=630162a2-b00a-48ef-831b-4d800b6db70c";/////////////////////////sửa lại
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);

        imageView=(ImageView)findViewById(R.id.imgAvatar);

        Picasso.get().load(url)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
    }

    public void UpdateInfoClick(View view) {
        Intent edit_intent = new Intent(getApplicationContext(), UpdateInfoAccount.class);
        startActivity(edit_intent);
    }
}
