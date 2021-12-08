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


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    List<Cart> list = new ArrayList<>();
    DatabaseReference cartRef;
    String uid = FirebaseAuth.getInstance().getUid();
    public CartAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
        cartRef = FirebaseDatabase.getInstance().getReference("list_user").child(uid).child("cart");
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
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
        holder.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.getProductAmount()<99){
                    cartRef.child(cart.getCartId()).child("productAmount").setValue(cart.getProductAmount()+1);
                } else {
                    Toast.makeText(context, "Số lượng món tối đa là 99", Toast.LENGTH_SHORT).show();
                }
                holder.tvAmount.setText(cart.getProductAmount()+"");
            }
        });
        holder.ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.getProductAmount()>1){
                    cartRef.child(cart.getCartId()).child("productAmount").setValue(cart.getProductAmount()-1);
                } else {
                    Toast.makeText(context, "Số lượng món tối thiểu là 1", Toast.LENGTH_SHORT).show();
                }
                holder.tvAmount.setText(cart.getProductAmount()+"");
            }
        });
        holder.tvPrice.setText(cart.getProductPrice()+" vnd");
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartRef.child(cart.getCartId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                        Toast.makeText(context, "Đã gỡ món ra khỏi giỏ hàng"+position, Toast.LENGTH_SHORT).show();
                    }
                });
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
        ImageView ivProduct, ivRemove, ivMinus, ivPlus;
        TextView tvName, tvAmount, tvPrice;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct_Item_Cart);
            ivRemove = itemView.findViewById(R.id.remove_Item_Cart);
            ivMinus = itemView.findViewById(R.id.minus_Item_Cart);
            ivPlus = itemView.findViewById(R.id.plus_Item_Cart);
            tvName = itemView.findViewById(R.id.tvName_Item_Cart);
            tvAmount = itemView.findViewById(R.id.amount_Item_Cart);
            tvPrice = itemView.findViewById(R.id.tvPrice_Item_Cart);
        }
    }
}