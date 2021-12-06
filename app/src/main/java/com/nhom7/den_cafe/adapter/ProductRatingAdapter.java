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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.ProductCount;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductRatingAdapter extends RecyclerView.Adapter<ProductRatingAdapter.ViewHolder> {
    Context context;
    List<Product> list = new ArrayList<>();
    public ProductRatingAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_amount, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Product product = list.get(position);
        Glide
                .with(context)
                .load(product.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.ivProduct);
        holder.tvName.setText(product.getProductName());
        holder.tvPrice.setText(product.getProductPrice()+"");
        DecimalFormat df = new DecimalFormat("#.0");
        holder.tvAmount.setText(df.format(product.getRating())+"");
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvAmount;
        ImageView ivProduct;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameItemProductAmount);
            tvPrice = itemView.findViewById(R.id.tvPriceItemProductAmount);
            tvAmount = itemView.findViewById(R.id.tvAmountItemProductAmount);
            ivProduct = itemView.findViewById(R.id.ivItemProductAmount);
        }
    }
}
