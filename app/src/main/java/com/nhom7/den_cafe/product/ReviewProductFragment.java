package com.nhom7.den_cafe.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.ReviewAdapter;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.ProductRating;
import com.nhom7.den_cafe.model.RateUnit;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewProductFragment extends Fragment {
    View view;
    ImageView ivBack;
    CardView cvReview;
    RecyclerView rcv;
    Product mProduct;
    List<ProductRating> productRatingList = new ArrayList<>();
    List<RateUnit> rateUnitList = new ArrayList<>();
    List<Product> productList = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getUid();;
    DatabaseReference productRate;
    DatabaseReference userRef;
    float count, total, average;
    ReviewAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_product, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivBack = view.findViewById(R.id.ivBackRPF);
        cvReview = view.findViewById(R.id.cvReviewRPF);
        rcv = view.findViewById(R.id.rcvRPF);
        Bundle bundle = getArguments();
        mProduct = (Product) bundle.getSerializable("product");

        userRef = FirebaseDatabase.getInstance().getReference("list_user");
        productRate = FirebaseDatabase.getInstance().getReference("rating");
        getUserRatingList();
        getProductRatingList();
        getProductList();
        getUserList();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailProductFragment fragment = new DetailProductFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", mProduct);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
            }
        });
        cvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogReview();
            }
        });
        adapter = new ReviewAdapter(getContext(), rateUnitList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        rcv.setAdapter(adapter);
    }

    private void openDialogReview(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.dialog_alert);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_review, null);
        builder.setTitle(null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        RatingBar ratingBar = view.findViewById(R.id.rateRD);
        TextInputLayout edContent = view.findViewById(R.id.edContentRD);
        CardView cvReview = view.findViewById(R.id.cvReviewRD);
        final Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String date = sdf.format(today);
        cvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos2 = 0, pos3 = 0;
                for(int i=0;i<userList.size();i++){
                    if(userList.get(i).getUserId().equals(uid)){
                        pos2 = i;
                    }
                }
                for(int j=0;j<productRatingList.size();j++){
                    if(productRatingList.get(j).getIdproduct().equals(mProduct.getProductId())){
                        pos3 = j;
                    }
                }
                String content = edContent.getEditText().getText().toString();
                float star = ratingBar.getRating();
                if(star>0){
                    count = productRatingList.get(pos3).getCount();
                    total = productRatingList.get(pos3).getTotal();
                    average = productRatingList.get(pos3).getAverage();
                    float newcount = count+1;
                    float newtotal = total+star;
                    float newaverage = newtotal/newcount;
                    RateUnit rateUnit = new RateUnit(uid, star, date, content, userList.get(pos2).getUserName());
                    rateUnitList.add(rateUnit);
                    ProductRating productRating = new ProductRating(productRatingList.get(pos3).getIdproduct(), newcount, newtotal, newaverage, rateUnitList);
                    int finalPos = pos3;
                    productRate.child(mProduct.getProductId()).setValue(productRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            int pos = 0;
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
                            for(int i=0;i<productList.size();i++){
                                if(productList.get(i).getProductId().equals(productRatingList.get(finalPos).getIdproduct())){
                                    pos = i;
                                }
                            }
                            databaseRef.child(productList.get(pos).getProductId()).child("rating").setValue(productRatingList.get(finalPos).getAverage());
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Bạn chưa chọn mức xếp hạng", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

    }

    private void getUserRatingList(){
        productRate.child(mProduct.getProductId()).child("rateUnits").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    rateUnitList.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        RateUnit rateUnit = dataSnapshot.getValue(RateUnit.class);
                        rateUnitList.add(rateUnit);
                    }
                    adapter.notifyDataSetChanged();
                }
                int t = 0;
                for(int i=0;i<rateUnitList.size();i++){
                    if(rateUnitList.get(i).getUidRating().equals(uid)) {
                        t = 1;
                    } else {
                        t = 0;
                    }
                }
                if(t==1) {
                    cvReview.setClickable(false);
                    cvReview.setBackgroundColor(R.color.Gray);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getProductRatingList(){
        productRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                productRatingList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ProductRating productRating  = dataSnapshot.getValue(ProductRating.class);
                    productRatingList.add(productRating);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getUserList(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getProductList(){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productList.add(product);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


}
