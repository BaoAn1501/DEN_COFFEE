package com.nhom7.den_cafe.product;

import android.content.Intent;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.home.AMProductFragment;
import com.nhom7.den_cafe.model.ProductType;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AddProductTypeFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 123;
    View view;
    ImageView ivBack, ivType;
    TextInputLayout edName;
    TextView tvAdd;
    CardView cvAdd;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private StorageTask storageTask;
    ProductType typeUD;
    int intype;
    private Uri mImageUri;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_producttype, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AMProductFragment());
            }
        });
        Bundle bundle = getArguments();
        intype = bundle.getInt("type");
        if(intype==1){

        }
        else {
            tvAdd.setText("Update");
            typeUD = (ProductType) bundle.getSerializable("product_type");
            edName.getEditText().setText(typeUD.getTypeName());
            Glide.with(this)
                    .load(typeUD.getTypeImage())
                    .centerCrop()
                    .into(ivType);
        }
        ivType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductType();
            }
        });
    }
    private void init(){
        ivBack = view.findViewById(R.id.ivBackAPTF);
        ivType = view.findViewById(R.id.ivTypeAPTF);
        edName = view.findViewById(R.id.edNameTypeAPTF);
        cvAdd = view.findViewById(R.id.cvAddAPTF);
        tvAdd = view.findViewById(R.id.tvAddAPTF);
        storageRef = FirebaseStorage.getInstance().getReference("imageFolder");
        databaseRef = FirebaseDatabase.getInstance().getReference("list_product_type");
    }

    private void addProductType() {
        if (mImageUri != null) {
            String key = UUID.randomUUID().toString();
            StorageReference fileReference = storageRef.child("imageType/" + key);
            storageTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();
                            String imageUrl = url.toString();
                            final ProductType type = new ProductType(
                                    edName.getEditText().getText().toString().trim(),
                                    imageUrl);
                            if(intype==1){
                                databaseRef.child(key).setValue(type);
                                Toast.makeText(getContext(), "Đã thêm loại sản phẩm", Toast.LENGTH_SHORT).show();
                                loadFragment(new AMProductFragment());
                            } else if(intype==2) {
                                databaseRef.child(typeUD.getTypeId()).setValue(type).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Đã cập nhật loại sản phẩm", Toast.LENGTH_SHORT).show();
                                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(typeUD.getTypeImage());
                                        imageRef.delete();
                                        loadFragment(new AMProductFragment());
                                    }
                                });
                            }


                        }
                    });
        } else {
            Toast.makeText(getContext(), "Bạn chưa chọn ảnh cho loại sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_AdminMain,fragment);
        transacion.commit();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            ivType.setImageURI(mImageUri);
            Glide.with(this).load(mImageUri).into(ivType);
        }
    }


}
