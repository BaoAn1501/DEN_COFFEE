package com.nhom7.den_cafe.product;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.home.AMProductFragment;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.ProductRating;
import com.nhom7.den_cafe.model.ProductType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 222;
    View view;
    ImageView ivBack, ivProduct;
    TextInputLayout edName, edPrice;
    Spinner spnType;
    CardView cvAdd, cvSpn;
    TextView tvAdd;
    private StorageReference storageRef;
    private DatabaseReference typeRef, productRef;
    List<Product> products = new ArrayList<>();
    List<ProductType> types = new ArrayList<>();
    private StorageTask storageTask;
    Product productUD;
    Uri mImageUri;
    ProgressDialog progressDialog;
    int intype;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
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
            spinnerTypeProduct();
        } else {
            tvAdd.setText("Update");
            productUD = (Product) bundle.getSerializable("product");
            edName.getEditText().setText(productUD.getProductName());
            edPrice.getEditText().setText(productUD.getProductPrice()+"");
            onViewSpinnerProductType();
            Glide.with(this)
                    .load(productUD.getProductImage())
                    .centerCrop()
                    .into(ivProduct);
        }
        ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooseImage();
            }
        });
        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateProductName()>0 && validateProductPrice()>0){
                    addProduct();
                }
            }
        });
    }
    private void init(){
        ivProduct = view.findViewById(R.id.ivProductAPF);
        ivBack = view.findViewById(R.id.ivBackAPF);
        edName = view.findViewById(R.id.edProductNameAPF);
        edPrice = view.findViewById(R.id.edProductPriceAPF);
        cvSpn = view.findViewById(R.id.cvSpnTypeAPF);
        spnType = view.findViewById(R.id.spnListTypeAPF);
        cvAdd = view.findViewById(R.id.cvAddAPF);
        tvAdd = view.findViewById(R.id.tvAddAPF);
        storageRef = FirebaseStorage.getInstance().getReference("imageFolder");
        typeRef = FirebaseDatabase.getInstance().getReference("list_product_type");
        productRef = FirebaseDatabase.getInstance().getReference("list_product");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_AdminMain,fragment);
        transacion.commit();
    }

    private void spinnerTypeProduct() {
        typeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final List<ProductType> spnList = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ProductType productType = dataSnapshot.getValue(ProductType.class);
                    spnList.add(productType);
                }
                ArrayAdapter<ProductType> arrayAdapter = new ArrayAdapter<ProductType>(getContext(), android.R.layout.simple_list_item_1, spnList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spnType.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void onViewSpinnerProductType() {
        typeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final List<ProductType> spnList = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ProductType productType = dataSnapshot.getValue(ProductType.class);
                    spnList.add(productType);
                }
                ArrayAdapter<ProductType> arrayAdapter = new ArrayAdapter<ProductType>(getContext(), android.R.layout.simple_list_item_1, spnList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spnType.setAdapter(arrayAdapter);
                for(int i =0;i<spnType.getCount();i++){
                    if(spnType.getItemAtPosition(i).toString().equals(productUD.getProductType())){
                        spnType.setSelection(i);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void openFileChooseImage() {
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
            ivProduct.setImageURI(mImageUri);
            Glide.with(this).load(mImageUri).into(ivProduct);
        }
    }

    private void addProduct() {
        if (mImageUri != null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Đang xử lý...");
            progressDialog.show();
            String key = UUID.randomUUID().toString();
            StorageReference fileReference = storageRef.child("imageProduct/" + key);
            storageTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();
                            String imageUrl = url.toString();
                            final Product product = new Product();
                            product.setProductId(key);
                            product.setProductName(edName.getEditText().getText().toString().trim());
                            product.setProductImage(imageUrl);
                            product.setProductPrice(Integer.parseInt(edPrice.getEditText().getText().toString().trim()));
                            product.setProductType(spnType.getSelectedItem().toString());
                            if(intype==1){
                                DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");
                                ProductRating productRating = new ProductRating(key, 0, 0, 0);
                                ratingRef.child(key).setValue(productRating);
                                productRef.child(key).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        loadFragment(new AMProductFragment());
                                        Toast.makeText(getContext(), "Đã thêm món", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if(intype==2) {
                                productUD.setProductName(edName.getEditText().getText().toString().trim());
                                productUD.setProductImage(imageUrl);
                                productUD.setProductPrice(Integer.parseInt(edPrice.getEditText().getText().toString().trim()));
                                productUD.setProductType(spnType.getSelectedItem().toString());
                                productRef.child(productUD.getProductId()).setValue(productUD).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(productUD.getProductImage());
                                        imageRef.delete();
                                        progressDialog.dismiss();
                                        loadFragment(new AMProductFragment());
                                        Toast.makeText(getContext(), "Đã cập nhật món", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                        }
                    });
        } else {
            Toast.makeText(getContext(), "Bạn chưa chọn ảnh cho món", Toast.LENGTH_SHORT).show();
        }
    }

    private int validateProductName(){
        int result = 1;
        if(edName.getEditText().getText().toString().trim().equals("")){
            edName.setError("Bạn chưa nhập tên cho món");
            result = 0;
        } else {
            edName.setErrorEnabled(false);
        }
        return result;
    }

    private int validateProductPrice(){
        int result = 1;
        if(edPrice.getEditText().getText().toString().trim().equals("")){
            edPrice.setError("Bạn chưa nhập giá cho món");
            result = 0;
        } else {
            edPrice.setErrorEnabled(false);
        }
        return result;
    }

}
