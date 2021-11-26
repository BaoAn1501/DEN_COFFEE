package com.nhom7.den_cafe.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.OrderAdapter;
import com.nhom7.den_cafe.adapter.OrderApprovalAdapter;
import com.nhom7.den_cafe.model.OrderState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AMOrderFragment extends Fragment {

    View view;
    RecyclerView rcv;
    List<OrderState> list = new ArrayList<>();
    OrderApprovalAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_am_order, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvAMOF);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        getListFromFirebase();
        adapter = new OrderApprovalAdapter(getContext(), list);
        rcv.setAdapter(adapter);
    }
    private void getListFromFirebase() {
        DatabaseReference orderStateRef = FirebaseDatabase.getInstance().getReference("list_order_state");
        orderStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    OrderState state = dataSnapshot.getValue(OrderState.class);
                    if(state.isState()==false){
                        list.add(state);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}