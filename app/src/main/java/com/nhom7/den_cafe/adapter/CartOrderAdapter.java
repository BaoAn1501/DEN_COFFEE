package com.nhom7.den_cafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Cart;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CartOrderAdapter extends RecyclerView.Adapter<CartOrderAdapter.ViewHolder> {
    Context context;
    List<Cart> list = new ArrayList<>();
    DatabaseReference cartRef;
    String uid = FirebaseAuth.getInstance().getUid();
    public CartOrderAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
        cartRef = FirebaseDatabase.getInstance().getReference("list_user").child(uid).child("cart");
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_order, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Cart cart = list.get(position);
        Glide
                .with(context)
                .load(cart.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.ivProduct);
        holder.tvName.setText(cart.getProductName());
        holder.tvAmount.setText(cart.getProductAmount()+"");
        holder.tvPrice.setText(cart.getProductPrice()+" vnd");

    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvAmount, tvPrice;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct_ItemCartOrder);
            tvName = itemView.findViewById(R.id.tvName_ItemCartOrder);
            tvAmount = itemView.findViewById(R.id.tvAmount_ItemCartOrder);
            tvPrice = itemView.findViewById(R.id.tvPrice_ItemCartOrder);
        }
    }
}
