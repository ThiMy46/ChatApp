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
import com.appchat.model.UserInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<UserInformation> listUser = new ArrayList<>();

    public CustomRecyclerSearchAdapter(List<UserInformation> listUser) {
        this.listUser = listUser;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        UserInformation user = listUser.get(position);
        final UsersViewHolder holder1=(UsersViewHolder)holder;
        holder1.txtName.setText(user.getName());
        Picasso.with(holder1.itemView.getContext())
                .load(user.getUrlAvatar().toString ())
                .into (holder1.user_image);

        // bắt sự kiện khi kích vào LinearLayout
        holder1.item_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null) {
                    onItemClickedListener.onItemClick(position);//click iteam RecyclerView hiển thị lastMessage
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if ( listUser!= null) {
            return listUser.size();
        }
        return 0;
    }




    // View Holder Class

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView txtName;
        ImageView user_image;
        ConstraintLayout item_user;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtName = (TextView) mView.findViewById(R.id.tvNameHeader);
            user_image = (ImageView) mView.findViewById(R.id.imgHinh);
            item_user=(ConstraintLayout) mView.findViewById(R.id.friend);
        }
    }

    //Sự kiện click vào item trên list
    public interface OnItemClickedListener {
        void onItemClick( int index);
    }

    private CustomRecyclerSearchAdapter.OnItemClickedListener onItemClickedListener;
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
