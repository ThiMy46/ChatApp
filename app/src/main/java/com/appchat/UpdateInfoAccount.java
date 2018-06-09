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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.appchat.model.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class UpdateInfoAccount extends AppCompatActivity {

    Button btnAvatar;
    ImageView avatar;
    Button btnSave;
    EditText Name;
    EditText Sdt;
    RadioButton rNam;
    RadioButton rNu;
    //
    int REQUEST;
    //Firebase Storage
    FirebaseAuth mAuth;
    UserInformation userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info_account);

        //Ánh xạ
        avatar = (ImageView)findViewById (R.id.avatar);
        btnAvatar=(Button)findViewById (R.id.btnAvatar);
        btnSave=(Button)findViewById (R.id.btnSave);
        Name = (EditText) findViewById (R.id.editName);
        Sdt=(EditText) findViewById (R.id.editSDT);
        rNam=(RadioButton) findViewById (R.id.rNam);
        rNu=(RadioButton) findViewById (R.id.rNu);

        mAuth = mAuth.getInstance ();


        //lấy info từ Info_Account gửi sang
        Intent edit_intent = getIntent();
        userInfo=new UserInformation();
        userInfo.setName(edit_intent.getStringExtra("name"));
        userInfo.setSdt(edit_intent.getStringExtra("sdt"));
        userInfo.setMail(edit_intent.getStringExtra("mail"));
        userInfo.setSex(edit_intent.getStringExtra("sex"));
        userInfo.setUrlAvatar(edit_intent.getStringExtra("urlAvatar"));

        Name.setText(userInfo.getName());
        Sdt.setText(edit_intent.getStringExtra("sdt"));
        String Sex=userInfo.getSex();
        if ( Sex == "Nam")
            rNam.setChecked (true);
        else
            rNu.setChecked (true);
        Picasso.with(getBaseContext()).load(userInfo.getUrlAvatar()).into (avatar);
        //

        //Button Avatar Click
        btnAvatar.setOnClickListener (new View.OnClickListener (){
            @Override
            public void onClick(View v){
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult (intent,REQUEST);
            }
        });//end Button Avatar click

        //Button Save click
        btnSave.setOnClickListener (new View.OnClickListener (){
            @Override
            public void onClick(View v){
                setAvatar();
            }
        });// end button Save click

        //add button Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    //Button Back
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
            avatar.setImageBitmap(bitmap);
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
        StorageReference mountainsRef = storageRef.child(user.getUid()).child ("avatar").child ("avatar"+calendar.getTimeInMillis ()+".png");

        // Get the data from an ImageView as bytes
        avatar.setDrawingCacheEnabled(true);
        avatar.buildDrawingCache();
        Bitmap bitmap = avatar.getDrawingCache ();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Upload Thất bại
                Toast.makeText (UpdateInfoAccount.this, "Lỗi!\n Hãy sử dụng hình ảnh khác.", Toast.LENGTH_SHORT).show ();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload Thành Công
                Toast.makeText (UpdateInfoAccount.this, "Thành công! \n Avatar của bạn đã được cập nhất.", Toast.LENGTH_SHORT).show ();
                //
                Uri downloadUrl= taskSnapshot.getDownloadUrl ();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser user = mAuth.getCurrentUser ();

                //lấy tất cả thay đổi(nếu có)
                userInfo.setName(Name.getText().toString());
                userInfo.setSdt(Sdt.getText().toString());
                if (rNam.isChecked ())
                    userInfo.setSex("Nam");
                else
                    userInfo.setSex("Nữ");
                userInfo.setUrlAvatar(downloadUrl+"");
                mDatabase.child ("users").child(user.getUid()).setValue(userInfo);
                finish();
            }
        });
    }

    //Cập nhập thông tin xong quay về trang Info
}
