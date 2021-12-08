package com.nhom7.den_cafe.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.CartAdapter;
import com.nhom7.den_cafe.home.UMProductFragment;
import com.nhom7.den_cafe.model.Cart;
import com.nhom7.den_cafe.order.ConfirmAddressFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    View view;
    RecyclerView rcv;
    List<Cart> list = new ArrayList<>();
    CartAdapter adapter;
    TextView tvTotal;
    ImageView ivBack;
    CardView cvOrder;
    int sum;
    String uid = FirebaseAuth.getInstance().getUid();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvCF);
        ivBack = view.findViewById(R.id.ivBackCF);
        tvTotal = view.findViewById(R.id.tvTotalCF);
        cvOrder = view.findViewById(R.id.cvOrderCF);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        getListFromFirebase();
        rcv.setLayoutManager(manager);
        adapter = new CartAdapter(getContext(), list);
        rcv.setAdapter(adapter);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UMProductFragment());
            }
        });
        cvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sum>0){
                    loadFragment(new ConfirmAddressFragment());
                } else {
                    Toast.makeText(getActivity(), "Bạn chưa có món nào", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getListFromFirebase() {
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
        transacion.commit();
    }
}
