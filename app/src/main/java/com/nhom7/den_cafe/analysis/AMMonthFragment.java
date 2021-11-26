package com.nhom7.den_cafe.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.nhom7.den_cafe.model.DateMoney;
import com.nhom7.den_cafe.model.OrderDetail;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AMMonthFragment extends Fragment {
    View view;
    List<OrderDetail> orderDetails = new ArrayList<>();
    List<DateMoney> moneyList = new ArrayList<>();
    TextView tvTotal;
    BarChart barChart;
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
        barChart = view.findViewById(R.id.barChartAMMF);
        getListDay();

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
                        for(int i=0;i<orderDetails.size();i++){
                            Date date = sdf.parse(orderDetails.get(i).getDate());
                            if(orderDetails.get(i).isState()==true){
                                if(date.getMonth()==month){
                                    sum = sum + orderDetails.get(i).getTotal();
                                }
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
    private void getListDay(){
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
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    final Date today = new Date();
                    int month = today.getMonth();
                    if(orderDetails.size()>0){
                        moneyList.add(new DateMoney(orderDetails.get(0).getDate(), orderDetails.get(0).getTotal()));
                    } else {

                    }
                    ArrayList<BarEntry> chartList = new ArrayList<>();
                    for(int i=0;i<moneyList.size();i++){
                        chartList.add(new BarEntry(sdf.parse(moneyList.get(i).getDate()).getDay(), moneyList.get(i).getSum()));
                    }
                    BarDataSet barDataSet = new BarDataSet(chartList, "Revenue");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(R.color.Black);
                    barDataSet.setValueTextSize(14f);
                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);
                    barChart.getXAxis().setGranularityEnabled(true);
//                    barChart.getXAxis().setValueFormatter(new IntegerFormatter());
                    barChart.getDescription().setText("Revenue By Day");
                    barChart.animateY(2000);
                } catch (Exception e){

                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public class IntegerFormatter extends ValueFormatter {
        private DecimalFormat mFormat;

        public IntegerFormatter() {
            mFormat = new DecimalFormat("###,##0");
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return mFormat.format(barEntry.getX());
        }
    }
}
