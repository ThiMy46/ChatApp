package com.appchat.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> listFriend = new ArrayList<>();

    public CustomRecyclerFriendAdapter(List<String> listFriend) {
        this.listFriend = listFriend;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final String id=listFriend.get(position);
        final ViewHolder holder1=(ViewHolder)holder;
        //holder1.txtID.setText(id);
        FirebaseDatabase.getInstance().getReference().child("users/"+id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("name").getValue(String.class);
                holder1.txtName.setText(value);
                Picasso.with(holder1.itemView.getContext())
                        .load(dataSnapshot.child("urlAvatar").getValue(String.class).toString ())
                        .into (holder1.imgAvatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // bắt sự kiện khi kích vào LinearLayout
        holder1.item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null) {
                    onItemClickedListener.onItemClick(holder1.txtName.getText().toString(),position);//click iteam RecyclerView hiển thị lastMessage
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listFriend != null) {
            return listFriend.size();
        }
        return 0;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgAvatar;
        //TextView txtID;
        ConstraintLayout item_friend;
        public ViewHolder(View view) {
            super(view);
            txtName=(TextView)view.findViewById(R.id.tvNameHeader);
            //txtID=(TextView)view.findViewById(R.id.tvID);
            item_friend=(ConstraintLayout) view.findViewById(R.id.friend);
            imgAvatar=(ImageView)view.findViewById(R.id.imgHinh);
        }
    }

    //Sự kiện click vào item trên list
    public interface OnItemClickedListener {
        void onItemClick(String username, int index);//////////////////////////////////////////////////////số thông tin sẽ đc truyền đi khi click vào item
    }

    private CustomRecyclerFriendAdapter.OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(CustomRecyclerFriendAdapter.OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
