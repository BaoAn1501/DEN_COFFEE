package com.nhom7.den_cafe.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.OrderApprovedAdapter;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AMAllTimeFragment extends Fragment {
    View view;
    List<OrderDetail> orderDetails = new ArrayList<>();
    TextView tvTotal;
    OrderApprovedAdapter adapter;
    List<OrderState> orderStateList = new ArrayList<>();
    RecyclerView rcv;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alltime_admin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotal = view.findViewById(R.id.tvTotalAMATF);
        rcv = view.findViewById(R.id.rcvAMATF);
        getTotalAllTime();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        getListState();
        adapter = new OrderApprovedAdapter(getContext(), orderStateList);
        rcv.setAdapter(adapter);
    }

    private void getTotalAllTime() {
        DatabaseReference orderDetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
        orderDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                orderDetails.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    OrderDetail orderDetail = dataSnapshot.getValue(OrderDetail.class);
                    orderDetails.add(orderDetail);
                }
                int sum = 0;
                for(int i=0;i<orderDetails.size();i++) {
                    if (orderDetails.get(i).isState() == true) {
                        sum = sum + orderDetails.get(i).getTotal();
                    }
                }
                tvTotal.setText(sum+"");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getListState(){
        DatabaseReference orderStateRef = FirebaseDatabase.getInstance().getReference("list_order_state");
        orderStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                orderStateList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    OrderState orderState = dataSnapshot.getValue(OrderState.class);
                    if(orderState.isState()==true){
                        orderStateList.add(orderState);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
