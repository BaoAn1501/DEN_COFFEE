package com.nhom7.den_cafe.product;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.SearchProductAdapter;
import com.nhom7.den_cafe.home.UMProductFragment;
import com.nhom7.den_cafe.model.Product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    View view;
    AutoCompleteTextView autoComplete;
    RecyclerView rcv;
    ImageView ivBack;
    SearchProductAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoComplete = view.findViewById(R.id.autoComplete);
        rcv = view.findViewById(R.id.rcvSearch);
        ivBack = view.findViewById(R.id.ivBackSF);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UMProductFragment());
            }
        });
        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getListAuto(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }

    private void getListAuto(CharSequence s) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("list_product");
        List<Product> productList = new ArrayList<>();
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    if(product.getProductName().contains(s)){
                        productList.add(product);
                    }
                    if(s.toString().trim().equals("")){
                        productList.clear();
                    }
                }
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                rcv.setLayoutManager(manager);
                adapter = new SearchProductAdapter(getContext(), productList);
                rcv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
        transacion.commit();
    }
}
