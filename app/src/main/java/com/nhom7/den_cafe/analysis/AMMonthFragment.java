package com.nhom7.den_cafe.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.OrderApprovedAdapter;
import com.nhom7.den_cafe.model.DateMoney;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AMMonthFragment extends Fragment {
    View view;
    List<OrderDetail> orderDetails = new ArrayList<>();
    List<DateMoney> moneyList;
    TextView tvTotal;
    OrderApprovedAdapter adapter;
    List<OrderState> orderStateList = new ArrayList<>();
    RecyclerView rcv;
    ImageView toChart;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_thismonth_admin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotal = view.findViewById(R.id.tvTotalAMMF);
        getTotalInMonth();
        rcv = view.findViewById(R.id.rcvAMMF);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        getListState();
        adapter = new OrderApprovedAdapter(getContext(), orderStateList);
        rcv.setAdapter(adapter);
        toChart = view.findViewById(R.id.toChartAMMF);
        toChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AMMonthChartFragment());
            }
        });
    }

    private void getTotalInMonth() {
            DatabaseReference orderDetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
            orderDetailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    orderDetails.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        OrderDetail orderDetail = dataSnapshot.getValue(OrderDetail.class);
                        orderDetails.add(orderDetail);
                    }
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        final Date today = new Date();
                        int month = today.getMonth();
                        int sum = 0;
                        moneyList = new ArrayList<>();
                        for(int i=0;i<orderDetails.size();i++){
                            if(orderDetails.get(i).isState()==true && sdf.parse(orderDetails.get(i).getDate()).getMonth()==month){
                                sum = sum + orderDetails.get(i).getTotal();
                            }
                        }
                        tvTotal.setText(sum+"");
                    } catch (Exception e){

                    }

                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
    }

    private void getListState() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final Date today = new Date();
        int month = today.getMonth();
        DatabaseReference orderStateRef = FirebaseDatabase.getInstance().getReference("list_order_state");
        orderStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    orderStateList.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        OrderState orderState = dataSnapshot.getValue(OrderState.class);
                        if(orderState.isState()==true && sdf.parse(orderState.getDate()).getMonth()==month){
                            orderStateList.add(orderState);
                        }

                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e){

                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_AdminMain,fragment);
        transacion.commit();
    }

}
