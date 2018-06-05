package com.appchat.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appchat.R;
import com.appchat.model.Chats;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CusReListFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Chats> listChats=new ArrayList<>();

    public CusReListFriendAdapter(List<Chats> listData) {
        this.listChats = listData;
    }

    public void add(Chats chat) {
        listChats.add(chat);
        notifyItemInserted(listChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Chats chat = listChats.get(position);

        final ViewHolder holder1=(ViewHolder)holder;
        holder1.txtName.setText("Dao My");//////////////////////////////////////////sửa lại
        holder1.txtLastMessage.setText(chat.getLastMessage());
        holder1.txtTime.setText((DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(new Date(listChats.get(position).getTimestamp())))
                .toString());

        // bắt sự kiện khi kích vào LinearLayout
        holder1.item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null) {
                    onItemClickedListener.onItemClick(position+"");//click iteam RecyclerView hiển thị lastMessage
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listChats != null) {
            return listChats.size();
        }
        return 0;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLastMessage;
        TextView txtTime;
        TextView txtName;
        ConstraintLayout item_friend;
        public ViewHolder(View itemview) {
            super(itemview);
            txtName=(TextView)itemview.findViewById(R.id.tvName);
            txtLastMessage = (TextView) itemview.findViewById(R.id.tvLastMessage);
            txtTime = (TextView) itemview.findViewById(R.id.tvTime);
            item_friend=(ConstraintLayout) itemview.findViewById(R.id.item_friend);
        }
    }

    //Sự kiện click vào item trên list
    public interface OnItemClickedListener {
        void onItemClick(String username);//////////////////////////////////////////////////////số thông tin sẽ đc truyền đi khi click vào item
    }

    private OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
