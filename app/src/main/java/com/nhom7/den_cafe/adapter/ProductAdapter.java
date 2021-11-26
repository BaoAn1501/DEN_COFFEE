package com.nhom7.den_cafe.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.nhom7.den_cafe.model.ProductType;
import com.nhom7.den_cafe.product.AddProductFragment;
import com.nhom7.den_cafe.product.AddProductTypeFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context context;
    List<Product> list;
    public ProductAdapter(Context context, List<Product> list) {
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
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
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
                bottom_sheet_dialog(product);
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

    public void bottom_sheet_dialog(Product product){
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.bottom_sheet_choose, null);
        dialog.setContentView(view);
        CardView cvEdit = (CardView) view.findViewById(R.id.cvEdit_BottomChoose);
        CardView cvRemove = (CardView) view.findViewById(R.id.cvRemove_BottomChoose);
        cvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductFragment fragment = new AddProductFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type",2);
                bundle.putSerializable("product", product);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutAMPDF, fragment, null).commit();
                dialog.dismiss();
            }
        });
        cvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog_DeleteProduct(product);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openDialog_DeleteProduct(Product product){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog_alert);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setMessage("Bạn có muốn xoá sản phẩm này không ?");
        builder.setCancelable(false);
        builder.setNegativeButton("không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct(product);
            }
        }).show();
    }

    private void deleteProduct(Product product) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(product.getProductImage());
        databaseRef.child(product.getProductId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                storageRef.delete();
                ratingRef.child(product.getProductId()).removeValue();
                Toast.makeText(context, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
