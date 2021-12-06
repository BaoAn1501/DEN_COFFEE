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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.OrderApprovedAdapter;
import com.nhom7.den_cafe.home.AMAnalysisFragment;
import com.nhom7.den_cafe.model.DateMoney;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AMMonthChartFragment extends Fragment {
    View view;
    BarChart barChart;
    List<OrderDetail> orderDetails = new ArrayList<>();
    List<DateMoney> moneyList;
    ArrayList<BarEntry> chartList = new ArrayList<>();
    ImageView ivBack;
    String[] days = new String[7];
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_month_chart, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barChart = view.findViewById(R.id.bcMonthAMMCF);
        ivBack = view.findViewById(R.id.ivBackAMMCF);
        getTotalInDay();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AMAnalysisFragment());
            }
        });
    }

    private void getDayWeek(){
        moneyList = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta );
        for (int i = 0; i < 7; i++)
        {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        for (int i = 0; i < days.length; i++)
        {
            moneyList.add(i, new DateMoney(days[i], 0));
        }
    }

//    private void getTotalInWeek(){
//        for(int i=0;i<moneyList.size();i++){
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            ArrayList<BarEntry> chartList = new ArrayList<>();
//            try {
//                chartList.add(new BarEntry(sdf.parse(moneyList.get(i).getDate()).getDay(), moneyList.get(i).getSum()));
//                BarDataSet barDataSet = new BarDataSet(chartList, "Revenue");
//                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//                barDataSet.setValueTextColor(R.color.Black);
//                barDataSet.setValueTextSize(14f);
//                BarData barData = new BarData(barDataSet);
//                barChart.setData(barData);
//                barChart.getXAxis().setGranularityEnabled(true);
////                    barChart.getXAxis().setValueFormatter(new IntegerFormatter());
//                barChart.getDescription().setText("Revenue By Day");
//                barChart.animateY(2000);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    private void getTotalInDay() {
        getDayWeek();
        int i;
        for(i=0;i<moneyList.size();i++){
            DatabaseReference orderDetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
            int finalI = i;
            orderDetailRef.orderByChild("date").startAt(days[finalI]+ " 00:00:00")
                    .endAt(days[finalI]+" 23:59:59")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            try {
                                orderDetails.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    OrderDetail orderDetail = dataSnapshot.getValue(OrderDetail.class);
                                    orderDetails.add(orderDetail);
                                }
                                int sum = 0;
                                for (int j = 0; j < orderDetails.size(); j++) {
                                    if (orderDetails.get(j).isState() == true) {
                                        sum = sum + orderDetails.get(j).getTotal();
                                    }
                                }
                                moneyList.add(finalI, new DateMoney(days[finalI], sum));
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                chartList.add(new BarEntry(sdf.parse(moneyList.get(finalI).getDate()).getDay(), moneyList.get(finalI).getSum()));
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
                moneyList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final Date today = new Date();
                int month = today.getMonth();
                ArrayList<BarEntry> chartList = new ArrayList<>();
                try {
                    for(int i=0;i<orderDetails.size();i++){
                        if(orderDetails.get(i).isState()==true && sdf.parse(orderDetails.get(i).getDate()).getMonth()==month){
                            if(!(sdf.parse(moneyList.get(i).getDate())==sdf.parse(orderDetails.get(i).getDate()))){
                                moneyList.add(new DateMoney(orderDetails.get(i).getDate(), orderDetails.get(i).getTotal()));
                            } else {
                                for(int j=0;j<moneyList.size();j++){
                                    if(sdf.parse(moneyList.get(j).getDate())==sdf.parse(orderDetails.get(i).getDate())){
                                        moneyList.get(j).setSum(orderDetails.get(i).getTotal()+moneyList.get(j).getSum());
                                    }
                                }
                            }
                        }
                    }

                    for(int i=0;i<moneyList.size();i++){
                        if(sdf.parse(moneyList.get(i).getDate()).getMonth()==month){
                            chartList.add(new BarEntry(sdf.parse(moneyList.get(i).getDate()).getDay(), moneyList.get(i).getSum()));
                        }
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_AdminMain,fragment);
        transacion.commit();
    }
}
