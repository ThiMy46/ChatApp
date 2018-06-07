package com.appchat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appchat.MessageList;
import com.appchat.R;
import com.appchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Message chat = listMessage.get(position);

        myChatViewHolder.tvMessage.setText(chat.getMessage());
        myChatViewHolder.tvTime.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(new Date(listMessage.get(position).getTimestamp())))
                .toString() );
    }

    //gán dữ liệu cho ô bên trái
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
        otherChatViewHolder.tvMessage.setText(chat.getMessage());
        otherChatViewHolder.tvTime.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(new Date(listMessage.get(position).getTimestamp())))
                .toString() );
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

        public MyChatViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.text_message);
            tvTime = (TextView) itemView.findViewById(R.id.text_message_time);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMessage;
        TextView tvTime;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.text_message_name);
            tvMessage = (TextView) itemView.findViewById(R.id.text_message);
            tvTime = (TextView) itemView.findViewById(R.id.text_message_time);
        }
    }
}
