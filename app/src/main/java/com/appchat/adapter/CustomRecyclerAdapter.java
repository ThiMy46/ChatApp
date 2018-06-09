package com.appchat.adapter;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appchat.MessageList;
import com.appchat.R;
import com.appchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> listMessage = new ArrayList<>();

    private final String AuthID=FirebaseAuth.getInstance().getCurrentUser().getUid();

    public CustomRecyclerAdapter(List<Message> listData) {
        this.listMessage = listData;
    }

    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class

    private final Handler handler = new Handler();

    //onCreateViewHolder() dùng để gán giao diện tương ứng cho RecyclerView
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 1://ME
                //item người gửi, ô bên phải
                View viewChatMine = inflater.inflate(R.layout.item_message_send, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case 2://OTHER
                //item người khác gửi cho mình, ô bên trái
                View viewChatOther = inflater.inflate(R.layout.item_message_received, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    //onBindViewHolder() dùng để gán dữ liệu từ listMessage vào viewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if ((listMessage.get(position).getUsersID()).equals(AuthID)) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    //gán dữ liệu cho ô bên phải
    @SuppressLint("ClickableViewAccessibility")
    private void configureMyChatViewHolder(final MyChatViewHolder myChatViewHolder, int position) {
        Message chat = listMessage.get(position);

        //
        final String message=chat.getMessage();
        if(URLUtil.isValidUrl(message)&&chat.getType().trim().equals("image"))
        {
            //ẩn text
            myChatViewHolder.tvMessage.setVisibility(View.GONE);
            myChatViewHolder.tvTime.setText("");
            //hiện Image
            myChatViewHolder.imgSe.setVisibility(View.VISIBLE);
            //ẩn audio
            myChatViewHolder.audio_seerbar.setVisibility(View.GONE);
            myChatViewHolder.btnPhay.setVisibility(View.GONE);
            myChatViewHolder.audio_time.setText("");

            Picasso.with(myChatViewHolder.itemView.getContext())
                    .load(chat.getMessage())
                    .into(myChatViewHolder.imgSe);
            myChatViewHolder.img_time.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(new Date(listMessage.get(position).getTimestamp())))
                    .toString() );
        }
        else if(URLUtil.isValidUrl(message)&&chat.getType().trim().equals("audio"))
        {
            //ẩn text
            myChatViewHolder.tvMessage.setVisibility(View.GONE);
            myChatViewHolder.tvTime.setText("");
            //ẩn img
            myChatViewHolder.imgSe.setVisibility(View.GONE);
            myChatViewHolder.img_time.setText("");
            //hiện audio
            myChatViewHolder.audio_seerbar.setVisibility(View.VISIBLE);
            myChatViewHolder.btnPhay.setVisibility(View.VISIBLE);

            myChatViewHolder.audio_time.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(new Date(listMessage.get(position).getTimestamp())))
                    .toString() );

            myChatViewHolder.btnPhay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** ImageButton onClick event handler. Method which start/pause mediaplayer playing */
                    try {
                        mediaPlayer.setDataSource(message); // setup song from https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                        mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }else {
                        mediaPlayer.pause();
                    }
                    primarySeekBarProgressUpdater(myChatViewHolder);
                }
            });

            myChatViewHolder.audio_seerbar.setMax(99); // It means 100% .0-99
            myChatViewHolder.audio_seerbar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
                    if(mediaPlayer.isPlaying()){
                        SeekBar sb = (SeekBar)v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                    return false;
                }
            });

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
                    myChatViewHolder.audio_seerbar.setSecondaryProgress(percent);
                }
            });

        }
        else
        {
            //ẩn image
            myChatViewHolder.imgSe.setVisibility(View.GONE);
            myChatViewHolder.img_time.setText("");
            //hiện text
            myChatViewHolder.tvMessage.setVisibility(View.VISIBLE);
            //ẩn audio
            myChatViewHolder.audio_seerbar.setVisibility(View.GONE);
            myChatViewHolder.btnPhay.setVisibility(View.GONE);
            myChatViewHolder.audio_time.setText("");

            myChatViewHolder.tvMessage.setText(message);
            myChatViewHolder.tvTime.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(new Date(listMessage.get(position).getTimestamp())))
                    .toString() );
        }

    }

    //gán dữ liệu cho ô bên trái
    @SuppressLint("ClickableViewAccessibility")
    private void configureOtherChatViewHolder(final OtherChatViewHolder otherChatViewHolder, int position) {
        Message chat = listMessage.get(position);
        //lấy thông tin name của UserID tương ứng
        FirebaseDatabase.getInstance().getReference().child("users/"+chat.getUsersID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("name").getValue(String.class);
                otherChatViewHolder.tvName.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //
        final String message=chat.getMessage();
        if(URLUtil.isValidUrl(message)&&chat.getType().trim().equals("image"))
        {
            //ẩn text
            otherChatViewHolder.tvMessage.setVisibility(View.GONE);
            otherChatViewHolder.tvTime.setText("");
            //hiện image
            otherChatViewHolder.imgRe.setVisibility(View.VISIBLE);
            //ẩn audio
            otherChatViewHolder.audio_seerbar.setVisibility(View.GONE);
            otherChatViewHolder.btnPhay.setVisibility(View.GONE);
            otherChatViewHolder.audio_time.setText("");

            Picasso.with(otherChatViewHolder.itemView.getContext())
                    .load(chat.getMessage())
                    .into(otherChatViewHolder.imgRe);
            otherChatViewHolder.img_time.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(new Date(listMessage.get(position).getTimestamp())))
                    .toString() );

        }
        else if(URLUtil.isValidUrl(message)&&chat.getType().trim().equals("audio"))
        {
            //ẩn text
            otherChatViewHolder.tvMessage.setVisibility(View.GONE);
            otherChatViewHolder.tvTime.setText("");
            //ẩn img
            otherChatViewHolder.imgRe.setVisibility(View.GONE);
            otherChatViewHolder.img_time.setText("");
            //hiện audio
            otherChatViewHolder.audio_seerbar.setVisibility(View.VISIBLE);
            otherChatViewHolder.btnPhay.setVisibility(View.VISIBLE);

            otherChatViewHolder.audio_time.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(new Date(listMessage.get(position).getTimestamp())))
                    .toString() );
            otherChatViewHolder.btnPhay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** ImageButton onClick event handler. Method which start/pause mediaplayer playing */
                    try {
                        mediaPlayer.setDataSource(message); // setup song from https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                        mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }else {
                        mediaPlayer.pause();
                    }

                    primarySeekBarProgressUpdater(otherChatViewHolder);
                }
            });

            otherChatViewHolder.audio_seerbar.setMax(99); // It means 100% .0-99
            otherChatViewHolder.audio_seerbar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
                    if(mediaPlayer.isPlaying()){
                        SeekBar sb = (SeekBar)v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                    return false;
                }
            });

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
                    otherChatViewHolder.audio_seerbar.setSecondaryProgress(percent);
                }
            });
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
//                    buttonPlayPause.setImageResource(R.drawable.play);
//                }
//            });
        }
        else {
            //hiện text
            otherChatViewHolder.tvMessage.setVisibility(View.VISIBLE);
            //ẩn img
            otherChatViewHolder.imgRe.setVisibility(View.GONE);
            otherChatViewHolder.img_time.setText("");
            //ẩn audio
            otherChatViewHolder.audio_seerbar.setVisibility(View.GONE);
            otherChatViewHolder.btnPhay.setVisibility(View.GONE);
            otherChatViewHolder.audio_time.setText("");

            otherChatViewHolder.tvMessage.setText(chat.getMessage());///////////////////////////
            otherChatViewHolder.tvTime.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(new Date(listMessage.get(position).getTimestamp())))
                    .toString());
        }
    }
    public void add(Message chat) {
        listMessage.add(chat);
        notifyItemInserted(listMessage.size() - 1);
    }

    //trả về số lượng phần tử có trong list
    @Override
    public int getItemCount() {
        if (listMessage != null) {
            return listMessage.size();
        }
        return 0;
    }

    //xác định tin nhắn là của ai
    @Override
    public int getItemViewType(int position) {
        if ((listMessage.get(position).getUsersID()).equals(AuthID)) {
            return 1;//ME
        } else {
            return 2;//OTHER
        }
    }

    /**
     * ViewHolder for item view of list
     * */
    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTime;
        ImageView imgSe;
        TextView img_time;
        SeekBar audio_seerbar;
        ImageButton btnPhay;
        TextView audio_time;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.text_message);
            tvTime = (TextView) itemView.findViewById(R.id.text_message_time);
            imgSe=(ImageView) itemView.findViewById(R.id.imgSend);
            img_time=(TextView) itemView.findViewById(R.id.img_time);
            audio_seerbar=(SeekBar)itemView.findViewById(R.id.audioSeekBar);
            btnPhay=(ImageButton)itemView.findViewById(R.id.btnPlay);
            audio_time=(TextView)itemView.findViewById(R.id.audio_time);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMessage;
        TextView tvTime;
        ImageView imgRe;
        TextView img_time;
        SeekBar audio_seerbar;
        ImageButton btnPhay;
        TextView audio_time;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.text_message_name);
            tvMessage = (TextView) itemView.findViewById(R.id.text_message);
            tvTime = (TextView) itemView.findViewById(R.id.text_message_time);
            imgRe=(ImageView) itemView.findViewById(R.id.imgRe);
            img_time=(TextView) itemView.findViewById(R.id.img_time);
            audio_seerbar=(SeekBar)itemView.findViewById(R.id.audioSeekBar);
            btnPhay=(ImageButton)itemView.findViewById(R.id.btnPlay);
            audio_time=(TextView)itemView.findViewById(R.id.audio_time);
        }
    }


    /** Method which updates the SeekBar primary progress by current song playing position*/
    private void primarySeekBarProgressUpdater(final MyChatViewHolder viewholder) {
        // This math construction give a percentage of "was playing"/"song length"
        viewholder.audio_seerbar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100));
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater(viewholder);
                }
            };
            handler.postDelayed(notification,1000);
        }
    }

    /** Method which updates the SeekBar primary progress by current song playing position*/
    private void primarySeekBarProgressUpdater(final OtherChatViewHolder viewholder) {
        // This math construction give a percentage of "was playing"/"song length"
        viewholder.audio_seerbar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100));
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater(viewholder);
                }
            };
            handler.postDelayed(notification,1000);
        }
    }

}
