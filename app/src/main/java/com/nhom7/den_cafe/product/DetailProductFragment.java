package com.nhom7.den_cafe.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.ChangeEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.home.UMOrderFragment;
import com.nhom7.den_cafe.home.UMProductFragment;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.ProductRating;
import com.nhom7.den_cafe.model.RateUnit;
import com.nhom7.den_cafe.model.Wish;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailProductFragment extends Fragment {
    View view;
    ImageView ivProduct, ivToWish, ivBack;
    TextView tvProductName, tvProductPrice, tvAverage, tvCount;
    CardView cvAdd, cvReview;
    DatabaseReference productRef, userRef;
    StorageReference imgRef;
    Product mProduct;
    String uid = FirebaseAuth.getInstance().getUid();
    List<Cart> cartList = new ArrayList<>();
    List<Wish> wishList = new ArrayList<>();
    List<ProductRating> productRatingList = new ArrayList<>();
    DatabaseReference ratingRef;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_product, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        Bundle bundle = getArguments();
        mProduct = (Product) bundle.getSerializable("product");
        tvProductName.setText(mProduct.getProductName());
        tvProductPrice.setText(mProduct.getProductPrice()+" vnd");

        Glide
                .with(this)
                .load(mProduct.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(ivProduct);
        getCartList();
        getWishList();
        getProductRatingList();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UMProductFragment());
            }
        });
        ivToWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivToWish.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24_red).getConstantState()){
                    ivToWish.setImageResource(R.drawable.ic_baseline_favorite_24_red);
                    addToWishList();
                } else if(ivToWish.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_baseline_favorite_24_red).getConstantState()) {
                    ivToWish.setImageResource(R.drawable.ic_baseline_favorite_border_24_red);
                    removeWishList();
                }
            }
        });
        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoCart();
            }
        });
        cvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewProductFragment fragment = new ReviewProductFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", mProduct);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
            }
        });
    }

    private void init(){
        ivProduct = view.findViewById(R.id.ivProductDPF);
        ivToWish = view.findViewById(R.id.ivToWishDPF);
        tvProductName = view.findViewById(R.id.tvProductNameDPF);
        tvProductPrice = view.findViewById(R.id.ivPriceDPF);
        tvAverage = view.findViewById(R.id.tvAverageRatingDPF);
        tvCount = view.findViewById(R.id.tvCountRatingDPF);
        ivBack = view.findViewById(R.id.ivBackDPF);
        cvAdd = view.findViewById(R.id.cvToCartDPF);
        cvReview = view.findViewById(R.id.cvReviewDPF);
        productRef = FirebaseDatabase.getInstance().getReference("list_product");
        imgRef = FirebaseStorage.getInstance().getReference("imageFolder");
        userRef = FirebaseDatabase.getInstance().getReference("list_user");
        ratingRef = FirebaseDatabase.getInstance().getReference("rating");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
        transacion.commit();
    }

    private void getCartList(){
        DatabaseReference cartRef = userRef.child(uid).child("cart");
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                cartList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Cart cart = dataSnapshot.getValue(Cart.class);
                    cartList.add(cart);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getWishList() {
        DatabaseReference wishRef = userRef.child(uid).child("wish");
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
                    if(wishList.get(i).getIdWish().equals(mProduct.getProductId())){
                        result = 1;
                    }
                }
                if(result==1){
                    ivToWish.setImageResource(R.drawable.ic_baseline_favorite_24_red);
                } else {
                    ivToWish.setImageResource(R.drawable.ic_baseline_favorite_border_24_red);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void addToWishList(){
        DatabaseReference wishRef = userRef.child(uid).child("wish");
        Wish wish = new Wish(mProduct.getProductId(), mProduct.getProductName(), mProduct.getProductPrice(), mProduct.getProductImage());
        wishRef.child(wish.getIdWish()).setValue(wish);
    }

    private void removeWishList(){
        DatabaseReference wishRef = userRef.child(uid).child("wish");
        wishRef.child(mProduct.getProductId()).removeValue();
    }

    private void addtoCart(){
        DatabaseReference cartRef = userRef.child(uid).child("cart");
        if(cartList.size()==0){
            Toast.makeText(getActivity(), "new item / null", Toast.LENGTH_SHORT).show();
            String key = cartRef.push().getKey();
            Cart cart = new Cart(key, mProduct.getProductName(), mProduct.getProductPrice(), 1, mProduct.getProductImage());
            cartRef.child(key).setValue(cart);
            loadFragment(new CartFragment());
        } else {
            boolean exist = false;
            int pos = 0;
            for(int i=0;i<cartList.size();i++){
                if(cartList.get(i).getProductName().equals(mProduct.getProductName())){
                    pos = i;
                    exist = true;
                }
            }
            if(exist==true){
                Cart cart = new Cart(cartList.get(pos).getCartId(), mProduct.getProductName(), mProduct.getProductPrice(), cartList.get(pos).getProductAmount()+1, mProduct.getProductImage());
                cartRef.child(cartList.get(pos).getCartId()).setValue(cart);
                loadFragment(new CartFragment());
            } else {
                String key = cartRef.push().getKey();
                Cart cart = new Cart(key, mProduct.getProductName(), mProduct.getProductPrice(), 1, mProduct.getProductImage());
                cartRef.child(key).setValue(cart);
                loadFragment(new CartFragment());
            }
        }
    }

    private void getProductRatingList(){
        DatabaseReference productRate = FirebaseDatabase.getInstance().getReference("rating");
        productRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                productRatingList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ProductRating productRating  = dataSnapshot.getValue(ProductRating.class);
                    productRatingList.add(productRating);
                }
                int pos = -1;
                for(int i=0;i<productRatingList.size();i++){
                    if(productRatingList.get(i).getIdproduct().equals(mProduct.getProductId())){
                        pos = i;
                    }
                }
                if(pos>=0){
                    DecimalFormat df = new DecimalFormat("#.0");
                    tvAverage.setText(df.format(productRatingList.get(pos).getAverage())+"");
                    tvCount.setText(productRatingList.get(pos).getCount()+"");
                } else {
                    tvAverage.setText(0+"");
                    tvCount.setText(0+"");
                }



            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}
