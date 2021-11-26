package com.nhom7.den_cafe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.OrderState;
import com.nhom7.den_cafe.order.MoreDetailOrderFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderApprovedAdapter extends RecyclerView.Adapter<OrderApprovedAdapter.ViewHolder> {
    Context context;
    List<OrderState> list = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getUid();
    public OrderApprovedAdapter(Context context, List<OrderState> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_approval_state, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        OrderState state = list.get(position);
        holder.tvDate.setText(state.getDate());
        holder.tvState.setText("Đã duyệt");
        holder.tvState.setTextColor(R.color.Green);
        holder.tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreDetailOrderFragment fragment = new MoreDetailOrderFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("from",3);
                bundle.putSerializable("state", state);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_AdminMain, fragment, null).commit();
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
        ImageView ivCheck;
        TextView tvDate, tvState, tvShow;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(R.id.ivCheckItemOrderAState);
            tvDate = itemView.findViewById(R.id.tvDateItemOrderAState);
            tvState = itemView.findViewById(R.id.tvStateItemOrderAState);
            tvShow = itemView.findViewById(R.id.tvShowBillItemOrderAState);
        }
    }
}