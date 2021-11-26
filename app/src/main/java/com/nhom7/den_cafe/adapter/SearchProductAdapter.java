package com.nhom7.den_cafe.adapter;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.product.AddProductFragment;
import com.nhom7.den_cafe.product.DetailProductFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder> {
    Context context;
    List<Product> list;
    public SearchProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchProductAdapter.ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getProductName());
        holder.tvPrice.setText(product.getProductPrice()+"");
        Glide
                .with(context)
                .load(product.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.imageView);
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
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView tvName, tvPrice;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvItemProduct);
            tvName = itemView.findViewById(R.id.tvNameItemProduct);
            tvPrice = itemView.findViewById(R.id.tvPriceItemProduct);
            imageView = itemView.findViewById(R.id.ivItemProduct);
        }
    }
}