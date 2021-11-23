package com.nhom7.den_cafe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    Context context;
    List<OrderState> list = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getUid();
    public OrderAdapter(Context context, List<OrderState> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_state, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        OrderState state = list.get(position);
        holder.tvDate.setText(state.getDate());
        if(state.isState()==true) {
            holder.tvState.setText("Thành công");
            holder.tvState.setTextColor(R.color.Green);
            holder.tvShow.setVisibility(View.VISIBLE);
        } else {
            holder.tvState.setText("Đang duyệt");
            holder.tvState.setTextColor(R.color.Red);
            holder.tvShow.setVisibility(View.INVISIBLE);
        }
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        CardView cv;
        TextView tvDate, tvState, tvShow;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvItemOrderState);
            tvDate = itemView.findViewById(R.id.tvDateItemOrderState);
            tvState = itemView.findViewById(R.id.tvStateItemOrderState);
            tvShow = itemView.findViewById(R.id.tvShowBillItemOrderState);
        }
    }
}
