package com.nhom7.den_cafe.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.Wish;
import com.nhom7.den_cafe.product.DetailProductFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class WishAdapter extends RecyclerView.Adapter<WishAdapter.ViewHolder> {
    Context context;
    List<Wish> list = new ArrayList<>();
    DatabaseReference wishRef;
    String uid = FirebaseAuth.getInstance().getUid();
    public WishAdapter(Context context, List<Wish> list) {
        this.context = context;
        this.list = list;
        wishRef = FirebaseDatabase.getInstance().getReference("list_user").child(uid).child("wish");
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wish, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Wish wish = list.get(position);
        Glide
                .with(context)
                .load(wish.getProductImg())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.ivProduct);
        holder.tvName.setText(wish.getProductName());
        holder.tvPrice.setText(wish.getProductPrice()+"");
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishRef.child(wish.getIdWish()).removeValue();
            }
        });
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
                List<Product> productList = new ArrayList<>();
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Product product = postSnapshot.getValue(Product.class);
                            productList.add(product);
                        }
                        int result = 0;
                        Product product = new Product();
                        for(int i=0;i<productList.size();i++){
                            if(productList.get(i).getProductId().equals(wish.getIdWish())){
                                result = 1;
                                product = productList.get(i);
                            }
                        }
                        if (result==1){
                            DetailProductFragment fragment = new DetailProductFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("product", product);
                            fragment.setArguments(bundle);
                            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

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
        ImageView ivProduct, ivRemove;
        TextView tvName, tvPrice;
        CardView cv;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct_Item_Wish);
            ivRemove = itemView.findViewById(R.id.remove_Item_Wish);
            tvName = itemView.findViewById(R.id.tvName_Item_Wish);
            tvPrice = itemView.findViewById(R.id.tvPrice_Item_Wish);
            cv = itemView.findViewById(R.id.cvItemWish);
        }
    }
}
