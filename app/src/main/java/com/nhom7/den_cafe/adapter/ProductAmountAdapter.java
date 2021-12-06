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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.ProductCount;
import com.nhom7.den_cafe.order.MoreDetailOrderFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductAmountAdapter extends RecyclerView.Adapter<ProductAmountAdapter.ViewHolder> {
    Context context;
    List<ProductCount> list = new ArrayList<>();
    public ProductAmountAdapter(Context context, List<ProductCount> list) {
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
        ProductCount count = list.get(position);
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("list_product");
        List<Product> productList = new ArrayList<>();
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                int pos = 0;
                for(int i=0;i<productList.size();i++){
                    if(productList.get(i).getProductId().equals(count.getId())){
                        pos = i;
                    }
                }
                Glide
                        .with(context)
                        .load(productList.get(pos).getProductImage())
                        .centerCrop()
                        .placeholder(R.drawable.logoicon)
                        .into(holder.ivProduct);
                holder.tvName.setText(productList.get(pos).getProductName());
                holder.tvPrice.setText(productList.get(pos).getProductPrice()+"");
                holder.tvAmount.setText(count.getCount()+"");
                holder.ivStar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
        TextView tvName, tvPrice, tvAmount;
        ImageView ivProduct, ivStar;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameItemProductAmount);
            tvPrice = itemView.findViewById(R.id.tvPriceItemProductAmount);
            tvAmount = itemView.findViewById(R.id.tvAmountItemProductAmount);
            ivProduct = itemView.findViewById(R.id.ivItemProductAmount);
            ivStar = itemView.findViewById(R.id.star);
        }
    }
}

