package com.nhom7.den_cafe.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.nhom7.den_cafe.adapter.ListBillAdapter;
import com.nhom7.den_cafe.analysis.AMRevenueFragment;
import com.nhom7.den_cafe.analysis.AMTodayFragment;
import com.nhom7.den_cafe.home.AMAnalysisFragment;
import com.nhom7.den_cafe.home.AMOrderFragment;
import com.nhom7.den_cafe.home.UMOrderFragment;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoreDetailOrderFragment extends Fragment {
    View view;
    OrderState state;
    TextView tvDate, tvTotal, tvName, tvPhone, tvAddress;
    ImageView ivExit;
    List<OrderDetail> list = new ArrayList<>();
    OrderDetail detail;
    List<Cart> carts = new ArrayList<>();
    RecyclerView rcv;
    ListBillAdapter adapter;
    int from;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_more_detail_order_approval, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        Bundle bundle = getArguments();
        state = (OrderState) bundle.getSerializable("state");
        from = bundle.getInt("from");
        getOrderDetailFromFirebase();
        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from==1){
                    loadFragment1(new AMOrderFragment());
                } else if(from==2) {
                    loadFragment2(new UMOrderFragment());
                } else {
                    loadFragment1(new AMAnalysisFragment());
                }
            }
        });
    }

    private void init(){
        tvName = view.findViewById(R.id.tvNameMDOF);
        tvPhone = view.findViewById(R.id.tvPhoneMDOF);
        tvAddress = view.findViewById(R.id.tvAddressMDOF);
        tvDate = view.findViewById(R.id.tvDateMDOF);
        tvTotal = view.findViewById(R.id.tvTotalMDOF);
        rcv = view.findViewById(R.id.rcvMDOF);
        ivExit = view.findViewById(R.id.ivExitMDOF);
    }

    private void loadFragment1(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_AdminMain,fragment);
        transacion.commit();
    }

    private void loadFragment2(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
        transacion.commit();
    }

    private void getOrderDetailFromFirebase(){
        DatabaseReference orderdetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
        orderdetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    OrderDetail detail = dataSnapshot.getValue(OrderDetail.class);
                    list.add(detail);
                }
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getId().equals(state.getIdState())){
                        detail = list.get(i);
                    }
                }
                tvDate.setText(detail.getDate());
                tvName.setText(detail.getName());
                tvPhone.setText(detail.getPhone());
                tvAddress.setText(detail.getAddress());
                tvTotal.setText(detail.getTotal()+" vnd");
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                rcv.setLayoutManager(manager);
                DatabaseReference cartRef = orderdetailRef.child(state.getIdState()).child("cart");
                cartRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        carts.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Cart cart = dataSnapshot.getValue(Cart.class);
                            carts.add(cart);
                        }
                        adapter = new ListBillAdapter(getContext(), carts);
                        rcv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
