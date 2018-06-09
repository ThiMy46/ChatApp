package com.appchat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.appchat.model.Chats;
import com.appchat.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Audio extends AppCompatActivity {

    private static final String TAG = "AudioRecordTest";
    private String mFileName;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private AudioManager mAudioManager;
    Button btnSend;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    //
    FirebaseAuth mAuth;
    boolean flag=false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_audio);

        final ToggleButton mRecordButton = (ToggleButton) findViewById(R.id.record_button);
        final ToggleButton mPlayButton = (ToggleButton) findViewById(R.id.play_button);
        btnSend=(Button)findViewById(R.id.btnSend);
        mRecordButton.setEnabled(false);
        btnSend.setEnabled(false);

        if(checkPermissions()){
            mRecordButton.setEnabled(true);
            // Get AudioManager
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            // Request audio focus
            mAudioManager.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        mFileName= Environment
                .getExternalStorageDirectory().getAbsolutePath()
                +"/audiorecordtest"+(new Date().getTime())+".3gp";

        // Set up record Button
        mRecordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // Set enabled state
                mPlayButton.setEnabled(!isChecked);
                // Start/stop recording
                onRecordPressed(isChecked);
                flag=isChecked;
                if(!flag)
                {
                    btnSend.setEnabled(!flag);
                }
            }
        });

        // Set up play Button
        mPlayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // Set enabled state
                mRecordButton.setEnabled(!isChecked);
                // Start/stop playback
                onPlayPressed(isChecked);
            }
        });

//        // Get AudioManager
//        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//        // Request audio focus
//        mAudioManager.requestAudioFocus(afChangeListener,
//                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = mAuth.getInstance();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();
                Uri file = Uri.fromFile(new File(mFileName));
                final StorageReference riversRef = storageRef.child("audio/"+file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Audio.this, "Uploading failed", Toast.LENGTH_LONG).show();
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.e("Url", "DownloadUrl: "+downloadUrl);

                        Intent intent=getIntent();
                        String groupchats=intent.getStringExtra("GroupID");

                        long time=new Date().getTime();
                        Message messageToAdd = new Message(mAuth.getCurrentUser().getUid(),downloadUrl+"",time,"audio");
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
        });
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

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    // Toggle recording
    private void onRecordPressed(boolean shouldStartRecording) {

        if (shouldStartRecording) {
            startRecording();
        } else {
            stopRecording();
        }

    }

    // Start recording with MediaRecorder
    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't prepare and start MediaRecorder");
        }

        mRecorder.start();
    }

    // Stop recording. Release resources
    private void stopRecording() {

        if (null != mRecorder) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

    }

    // Toggle playback
    private void onPlayPressed(boolean shouldStartPlaying) {

        if (shouldStartPlaying) {
            startPlaying();
        } else {
            stopPlaying();
        }

    }

    // Playback audio using MediaPlayer
    private void startPlaying() {

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't prepare and start MediaPlayer");
        }

    }

    // Stop playback. Release resources
    private void stopPlaying() {
        if (null != mPlayer) {
            if (mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    // Listen for Audio Focus changes
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(afChangeListener);

                // Stop playback, if necessary
                if (null != mPlayer && mPlayer.isPlaying())
                    stopPlaying();
            }

        }

    };

    // Release recording and playback resources, if necessary
    @Override
    public void onPause() {
        super.onPause();

        if (null != mRecorder) {
            mRecorder.release();
            mRecorder = null;
        }

        if (null != mPlayer) {
            mPlayer.release();
            mPlayer = null;
        }

    }
}
