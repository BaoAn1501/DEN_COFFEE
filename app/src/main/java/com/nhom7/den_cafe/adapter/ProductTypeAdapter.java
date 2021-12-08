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
import com.nhom7.den_cafe.model.ProductType;
import com.nhom7.den_cafe.product.AddProductTypeFragment;

import java.util.List;

public class ProductTypeAdapter extends RecyclerView.Adapter<ProductTypeAdapter.ViewHolder> {
    Context context;
    List<ProductType> list;
    public ProductTypeAdapter(Context context, List<ProductType> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductTypeAdapter.ViewHolder holder, int position) {
        ProductType type = list.get(position);
        holder.tvName.setText(type.getTypeName());
        Glide
                .with(context)
                .load(type.getTypeImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.imageView);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_sheet_dialog(type);
            }
        });
//        context.startActivity(new Intent(context, AddProductTypeFragment.class).putExtra("type", type).putExtra("intype", 2));
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
        TextView tvName;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvItemType);
            tvName = itemView.findViewById(R.id.nameItemType);
            imageView = itemView.findViewById(R.id.imageItemType);
        }
    }

    private void deleteType(ProductType type){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product_type");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        String key = type.getTypeId();
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(type.getTypeImage());
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        list.remove(type.getTypeId());
                        Toast.makeText(context, "Đã xoá loại món", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void bottom_sheet_dialog(ProductType type){
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.bottom_sheet_choose, null);
        dialog.setContentView(view);
        CardView cvEdit = (CardView) view.findViewById(R.id.cvEdit_BottomChoose);
        CardView cvRemove = (CardView) view.findViewById(R.id.cvRemove_BottomChoose);
        cvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductTypeFragment fragment = new AddProductTypeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type",2);
                bundle.putSerializable("product_type", type);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutAMPDF, fragment, null).commit();
                dialog.dismiss();
            }
        });
        cvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog_DeleteType(type);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openDialog_DeleteType(ProductType type){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog_alert);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setMessage("Bạn có muốn xoá loại món này không ?");
        builder.setCancelable(false);
        builder.setNegativeButton("không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteType(type);
            }
        }).show();
    }
}
