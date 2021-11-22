package com.nhom7.den_cafe.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.Wish;
import com.nhom7.den_cafe.product.DetailProductFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder> {

    Context context;
    List<Product> list = new ArrayList<>();

    public ProductCategoryAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ProductCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_umain, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductCategoryAdapter.ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getProductName());
        holder.tvPrice.setText(product.getProductPrice()+" vnd");
        holder.ratingBar.setRating(product.getRating());
        Glide
                .with(context)
                .load(product.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.ivProduct);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailProductFragment fragment = new DetailProductFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
            }
        });
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        DatabaseReference wishRef = userRef.child(uid).child("wish");
        List<Wish> wishList = new ArrayList<>();
        wishRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                wishList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Wish wish = dataSnapshot.getValue(Wish.class);
                    wishList.add(wish);
                }
                int result = 0;
                for(int i=0;i<wishList.size();i++){
                    if(wishList.get(i).getIdWish().equals(product.getProductId())){
                        result = 1;
                    }
                }
                if(result==1){
                    holder.ivWish.setImageResource(R.drawable.ic_baseline_favorite_24_red);
                } else {
                    holder.ivWish.setImageResource(R.drawable.ic_baseline_favorite_border_24_red);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

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
        ImageView ivWish, ivProduct;
        RatingBar ratingBar;
        TextView tvName, tvPrice;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            ivWish = itemView.findViewById(R.id.tvToWish_Item_ProductM);
            ivProduct = itemView.findViewById(R.id.ivProduct_Item_ProductM);
            ratingBar = itemView.findViewById(R.id.rating_Item_ProductM);
            tvName = itemView.findViewById(R.id.ivName_Item_ProductM);
            tvPrice = itemView.findViewById(R.id.ivPrice_Item_ProductM);
            cv = itemView.findViewById(R.id.cv_Item_ProductM);
        }
    }
}
