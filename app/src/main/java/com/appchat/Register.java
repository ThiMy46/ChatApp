package com.appchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appchat.model.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity{

    String urlAvatarDefault="https://firebasestorage.googleapis.com/v0/b/appchat-d8ddb.appspot.com/o/default_image.png?alt=media&token=104d33cd-7d16-434b-a577-d18bec1d1f8c";
    TextView moveLogin;
    EditText mEditName, mEditNumber, mEditPass, mEditAgainPass;
    RadioGroup radioGroup;
    RadioButton sexNam;
    Button btnSign;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_register);

        //
        mAuth = FirebaseAuth.getInstance ();

        //
        //link đến Log In từ link TextView
        moveLogin = (TextView) findViewById (R.id.link_login);
        SpannableString spannableString = new SpannableString (getString (R.string.move_login));
        ClickableSpan clickableSpan = new ClickableSpan (){
            @Override
            public void onClick(View widget){
                startActivity ((new Intent (getApplicationContext (), Login.class)).setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish ();
            }
        };
        spannableString.setSpan (clickableSpan, spannableString.length ()-17,
                spannableString.length (), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        moveLogin.setText (spannableString, TextView.BufferType.SPANNABLE);
        moveLogin.setMovementMethod (LinkMovementMethod.getInstance ());
        //END

        //Ánh xạ
        mEditName = (EditText) findViewById (R.id.mEditName);
        mEditNumber = (EditText) findViewById (R.id.mEditNumber);
        mEditPass = (EditText) findViewById (R.id.mEditPass);
        mEditAgainPass = (EditText) findViewById (R.id.mEditAgainPass);
        sexNam = (RadioButton) findViewById (R.id.rNam);
        btnSign = (Button) findViewById (R.id.btn_signup);
        //onClick
        btnSign.setOnClickListener (new View.OnClickListener (){
            @Override
            public void onClick(View v){
                String User = mEditNumber.getText ().toString ();
                String Password = mEditPass.getText ().toString ();
                String Name = mEditName.getText ().toString ();
                String CheckPass = mEditAgainPass.getText ().toString ();
                if (CheckRegister (User, Password, CheckPass)) {
                    String Sex = new String ();
                    if (sexNam.isChecked ())
                        Sex = "Nam";
                    else
                        Sex = "Nữ";
                    Register (User, Password, Name, Sex);
                }
            }
        });
    }

    // Đăng ký
    private void Register(final String User, String Password, final String Name, final String Sex){

        mAuth.createUserWithEmailAndPassword (User, Password)
                .addOnCompleteListener (this, new OnCompleteListener<AuthResult> (){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful ()) {

                            //
                            FirebaseUser user = mAuth.getCurrentUser ();
                            UserInformation info = new UserInformation (Name, Sex, User, "none",urlAvatarDefault);
                            userInformation (user, info);
                            //
                            Toast.makeText (Register.this, "Đăng ký thành công! \nMời bạn đăng nhập lại", Toast.LENGTH_SHORT).show ();
                            Intent intent=new Intent(Register.this,Login.class);
                            startActivity(intent);
                        }else
                            Toast.makeText (Register.this, "Lỗi!", Toast.LENGTH_SHORT).show ();

                        // ... End complete Register
                    }
                });
    }

    private void userInformation(FirebaseUser user, UserInformation info){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance ().getReference ();
        databaseReference.child("users").child (user.getUid ()).setValue (info);
    }


    // Kiểm tra
    private boolean CheckRegister(String Name, String Password, String CheckPass){
        //

        if (TextUtils.isEmpty (Name)) {
            Toast.makeText (this, "Tên không hợp lệ!", Toast.LENGTH_SHORT).show ();
            return false;
        }

        if (Password.length () < 6) {
            Toast.makeText (this, "Mật khẩu phải ít nhất 6 kí tự!", Toast.LENGTH_SHORT).show ();
            return false;
        }
        if (Password.equals (CheckPass) == false) {
            Toast.makeText (this, "Mật khẩu không trùng!", Toast.LENGTH_SHORT).show ();
            return false;
        }
        return true;

    }
    //Đăng ký hoàn tất quay về trang LogIn
}

