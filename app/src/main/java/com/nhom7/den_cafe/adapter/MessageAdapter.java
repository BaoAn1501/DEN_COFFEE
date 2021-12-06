package com.nhom7.den_cafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Chat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    Context context;
    List<Chat> chatList = new ArrayList<>();
    private String imageUrl;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    public MessageAdapter(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @NotNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == MSG_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat=chatList.get(position);
        holder.show_message.setText(chat.getMessage());
        Glide
                .with(context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.profile_image);
        if(position==chatList.size()-1){
            if(chat.isIsseen()==true){
                holder.tvSeen.setText("Đã xem");
            } else {
                holder.tvSeen.setText("Đã nhận");
            }
        } else {
            holder.tvSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(chatList!=null){
            return chatList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message, tvSeen;
        public ImageView profile_image;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.tvMessage);
            profile_image = itemView.findViewById(R.id.ivProfileMessage);
            tvSeen = itemView.findViewById(R.id.tvSeenMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(chatList.get(position).getSender().equals(currentUser.getUid())){
            return MSG_RIGHT;
        }else {
            return MSG_LEFT;
        }
    }
}
