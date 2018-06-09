package com.appchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.appchat.model.Chats;
import com.appchat.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class Photo extends AppCompatActivity {

    ImageView imgHinh;
    Button Photo, Send;
    //
    FirebaseAuth mAuth;
    int REQUEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //ánh xạ
        imgHinh=(ImageView)findViewById(R.id.imgPhoto);
        Photo=(Button) findViewById(R.id.btnChup);
        Send=(Button)findViewById(R.id.btnSend);

        mAuth = mAuth.getInstance ();

        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult (intent,REQUEST);
            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(null);
    }

    //Chụp hình = máy ảnh của máy
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST && resultCode == RESULT_OK && data != null )
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgHinh.setImageBitmap(bitmap);
        }
        super.onActivityResult (requestCode, resultCode, data);
    }
    //end chụp hình

    void setAvatar(){
        Calendar calendar=Calendar.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Link storage của mình trên firebase
        StorageReference storageRef  = storage.getReferenceFromUrl ("gs://appchat-d8ddb.appspot.com");
        //
        StorageReference mountainsRef = storageRef.child("imgChats").child ("imgchat"+calendar.getTimeInMillis ()+".png");

        // Get the data from an ImageView as bytes
        imgHinh.setDrawingCacheEnabled(true);
        imgHinh.buildDrawingCache();
        Bitmap bitmap = imgHinh.getDrawingCache ();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Upload Thất bại
                Toast.makeText (Photo.this, "Lỗi!\n Hãy sử dụng hình ảnh khác.", Toast.LENGTH_SHORT).show ();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload Thành Công
                Toast.makeText (Photo.this, "Gửi Thành công!", Toast.LENGTH_SHORT).show ();
                //
                Uri downloadUrl= taskSnapshot.getDownloadUrl();

                Intent intent=getIntent();
                String groupchats=intent.getStringExtra("GroupID");

                long time=new Date().getTime();
                Message messageToAdd = new Message(mAuth.getCurrentUser().getUid(),downloadUrl+"",time,"image");
                Chats chat=new Chats(downloadUrl+"",time);

                FirebaseDatabase.getInstance().getReference().child("messages/"+groupchats)
                        .push()
                        .setValue(messageToAdd);
                FirebaseDatabase.getInstance().getReference().child("chats/"+groupchats)
                        .setValue(chat);
                finish();
            }
        });
    }

}
