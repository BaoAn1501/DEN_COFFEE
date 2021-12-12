package com.nhom7.den_cafe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.chat.MessageActivity;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserListChatAMAdapter extends RecyclerView.Adapter<UserListChatAMAdapter.ViewHolder> {
    Context context;
    List<User> list = new ArrayList<>();
    public UserListChatAMAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public UserListChatAMAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull @NotNull UserListChatAMAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        Glide
                .with(context)
                .load(user.getUserImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.ivAvatar);
        holder.tvName.setText(user.getUserName());
        int result = 0;
        if(user.isStatus()==true){
            result=1;
        }
        if(result==1){
            holder.ivStatus.setImageResource(R.drawable.ic_baseline_online);
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_baseline_offline);
        }
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MessageActivity.class).putExtra("from", 1).putExtra("userid", user.getUserId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivAvatar, ivStatus;
        CardView cv;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameUserChat);
            ivAvatar = itemView.findViewById(R.id.ivUserChat);
            ivStatus = itemView.findViewById(R.id.ivStatusUserChat);
            cv = itemView.findViewById(R.id.cvUserChat);
        }
    }
}