package com.nhom7.den_cafe.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.CartOrderAdapter;
import com.nhom7.den_cafe.home.UMProductFragment;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.model.OrderDetail;
import com.nhom7.den_cafe.model.OrderState;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailOrderFragment extends Fragment {
    View view;
    TextView tvName, tvPhone, tvAddress, tvTotal;
    ImageView ivBack, ivExit;
    RecyclerView rcv;
    CardView cvOrder;
    List<Cart> list = new ArrayList<>();
    CartOrderAdapter adapter;
    int sum;
    String name, phone, address;
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_order, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        phone = bundle.getString("phone");
        address = bundle.getString("address");
        tvName.setText(name);
        tvPhone.setText(phone);
        tvAddress.setText(address);
        showCartList();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        adapter = new CartOrderAdapter(getContext(), list);
        rcv.setAdapter(adapter);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ConfirmAddressFragment());
                ConfirmAddressFragment fragment = new ConfirmAddressFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("phone", phone);
                bundle.putString("address", address);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
            }
        });
        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UMProductFragment());
            }
        });
        cvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderProduct();
            }
        });
    }

    private void init(){
        tvName = view.findViewById(R.id.tvNameDOF);
        tvPhone = view.findViewById(R.id.tvPhoneDOF);
        tvAddress = view.findViewById(R.id.tvAddressDOF);
        tvTotal = view.findViewById(R.id.tvTotalDOF);
        ivBack = view.findViewById(R.id.ivBackDOF);
        ivExit = view.findViewById(R.id.ivExitDOF);
        rcv = view.findViewById(R.id.rcvDOF);
        cvOrder = view.findViewById(R.id.cvOrder_DOF);
    }

    private void showCartList() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference cartRef = userRef.child(uid).child("cart");
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                sum = 0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Cart cart = dataSnapshot.getValue(Cart.class);
                    cart.setCartId(dataSnapshot.getKey());
                    list.add(cart);
                    sum += cart.getProductPrice()*cart.getProductAmount();
                }
                tvTotal.setText(sum + "vnd");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void orderProduct(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference cartRef = userRef.child(uid).child("cart");
        DatabaseReference orderstateRef = FirebaseDatabase.getInstance().getReference("list_order_state");
        DatabaseReference orderdetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
        String key = orderstateRef.push().getKey();
        final Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String date = sdf.format(today);
        OrderState orderState = new OrderState(key, uid, currentUser.getPhoneNumber(), date, false);
        orderstateRef.child(key).setValue(orderState).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                OrderDetail detail = new OrderDetail(key, uid, phone, name, date, address, sum, false);
                orderdetailRef.child(key).setValue(detail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        orderdetailRef.child(key).child("cart").setValue(list);
                        cartRef.removeValue();
                        loadFragment(new UMProductFragment());
                    }
                });
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
        transacion.commit();
    }
}
