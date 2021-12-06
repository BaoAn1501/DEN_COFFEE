package com.nhom7.den_cafe.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.nhom7.den_cafe.adapter.ProductAmountAdapter;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.ProductCount;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AMProductAmountFragment extends Fragment {
    View view;
    RecyclerView rcv;
    List<OrderDetail> detailList = new ArrayList<>();
    List<ProductCount> countList = new ArrayList<>();
    List<Cart> cartList = new ArrayList<>();
    ProductAmountAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_productamount_admin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvAMPAF);
        getProductFromOrder();

    }

    private void getProductFromOrder(){
        addToRealtimeDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final Date today = new Date();
        int month = today.getMonth();
        DatabaseReference orderDetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
        orderDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    detailList.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        OrderDetail orderDetail = dataSnapshot.getValue(OrderDetail.class);
                        if (sdf.parse(orderDetail.getDate()).getMonth() == month && orderDetail.isState()==true) {
                            detailList.add(orderDetail);
                        }
                    }
                    for(int i=0;i<detailList.size();i++) {
                        orderDetailRef.child(detailList.get(i).getId()).child("cart").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                cartList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Cart cart = dataSnapshot.getValue(Cart.class);
                                    cartList.add(cart);
                                }
                                for (int i = 0; i < cartList.size(); i++) {
                                    for (int j = 0; j < countList.size(); j++) {
                                        if (countList.get(j).getId().equals(cartList.get(i).getCartId())) {
                                            countList.get(j).setCount(countList.get(j).getCount() + cartList.get(i).getProductAmount());
                                        }
                                    }
                                }
                                Comparator<ProductCount> sortList = new Comparator<ProductCount>(){
                                    @Override
                                    public int compare(ProductCount o1, ProductCount o2) {
                                        return new Integer(o1.getCount()).compareTo(new Integer(o2.getCount()));
                                    }
                                };
                                Collections.sort(countList, sortList);
                                Collections.reverse(countList);
                                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                                rcv.setLayoutManager(manager);
                                adapter = new ProductAmountAdapter(getContext(), countList);
                                rcv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
//                    adapter.notifyDataSetChanged();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addToRealtimeDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                countList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    countList.add(new ProductCount(product.getProductId(), 0));
                }
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
