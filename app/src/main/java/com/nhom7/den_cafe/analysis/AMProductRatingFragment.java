package com.nhom7.den_cafe.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.ProductRatingAdapter;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.ProductCount;
import com.nhom7.den_cafe.model.ProductRating;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AMProductRatingFragment extends Fragment {
    View view;
    RecyclerView rcv;
    ProductRatingAdapter adapter;
    List<Product> productList = new ArrayList<>();
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_productraing_analysis_admin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvAMPRF);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        getProductFromFirebase();
        adapter = new ProductRatingAdapter(getContext(), productList);
        rcv.setAdapter(adapter);
    }

    private void getProductFromFirebase(){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
        Query queryRating = databaseRef.orderByChild("rating");
        queryRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productList.add(0, product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
