package com.nhom7.den_cafe.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;
import com.nhom7.den_cafe.order.MoreDetailOrderFragment;
import com.nhom7.den_cafe.product.DetailProductFragment;

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
        holder.tvState.setText("Đang duyệt");
        holder.tvState.setTextColor(R.color.Red);
        holder.tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreDetailOrderFragment fragment = new MoreDetailOrderFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("from",2);
                bundle.putSerializable("state", state);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
            }
        });
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogRemoveItem(state);
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
        ImageView ivRemove;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvItemOrderState);
            tvDate = itemView.findViewById(R.id.tvDateItemOrderState);
            tvState = itemView.findViewById(R.id.tvStateItemOrderState);
            tvShow = itemView.findViewById(R.id.tvShowBillItemOrderState);
            ivRemove = itemView.findViewById(R.id.ivRemoveItemOrderState);
        }
    }

    private void openDialogRemoveItem(OrderState state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog_alert);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setMessage("Bạn có muốn huỷ đơn hàng này không ?");
        builder.setCancelable(false);
        builder.setNegativeButton("không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteState(state);
            }
        }).show();
    }

    private void deleteState(OrderState state){
        DatabaseReference orderstateRef = FirebaseDatabase.getInstance().getReference("list_order_state");
        DatabaseReference orderdetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
        orderstateRef.child(state.getIdState()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                orderdetailRef.child(state.getIdState()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Đã huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
