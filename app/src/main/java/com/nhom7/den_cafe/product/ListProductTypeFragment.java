package com.nhom7.den_cafe.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.ProductTypeAdapter;
import com.nhom7.den_cafe.model.ProductType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListProductTypeFragment extends Fragment {
    View view;
    RecyclerView rcv;
    FloatingActionButton fab;
    ProductTypeAdapter adapter;
    List<ProductType> list = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_producttype, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvLPTF);
        fab = view.findViewById(R.id.fabLPTF);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductTypeFragment fragment = new AddProductTypeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type",1);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutAMPDF, fragment, null).commit();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        addToRealtimeDatabase();
        adapter = new ProductTypeAdapter(getContext(), list);
        rcv.setAdapter(adapter);
    }

    private void addToRealtimeDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product_type");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ProductType type = postSnapshot.getValue(ProductType.class);
                    type.setTypeId(postSnapshot.getKey());
                    list.add(type);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
