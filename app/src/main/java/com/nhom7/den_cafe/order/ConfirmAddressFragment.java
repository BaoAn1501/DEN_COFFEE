package com.nhom7.den_cafe.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.home.UMProductFragment;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfirmAddressFragment extends Fragment {
    View view;
    TextInputLayout edName, edPhone, edAdress;
    CardView cvConfirm;
    String uid = FirebaseAuth.getInstance().getUid();
    ImageView ivBack;
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_confirm_address, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        Bundle bundle = getArguments();
        if(bundle!=null){
            String name = bundle.getString("name");
            String phone = bundle.getString("phone");
            String address = bundle.getString("address");
            edName.getEditText().setText(name);
            edPhone.getEditText().setText(phone);
            edAdress.getEditText().setText(address);
        } else {
            getUser();
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UMProductFragment());
            }
        });
        cvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateName()>0 && validatePhone()>0 && validateAddress()>0){
                    DetailOrderFragment fragment = new DetailOrderFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", edName.getEditText().getText().toString().trim());
                    bundle.putString("phone", edPhone.getEditText().getText().toString().trim());
                    bundle.putString("address", edAdress.getEditText().getText().toString().trim());
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
                }
            }
        });

    }
    private void init(){
        edAdress = view.findViewById(R.id.edAddress_CAF);
        edName = view.findViewById(R.id.edName_CAF);
        edPhone = view.findViewById(R.id.edPhone_CAF);
        cvConfirm = view.findViewById(R.id.cvConfirm_CAF);
        ivBack = view.findViewById(R.id.ivBackCAF);
    }

    private void getUser(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        List<User> users = new ArrayList<>();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }
                User user = null;
                for(int i =0;i<users.size();i++){
                    if(users.get(i).getUserId().equals(uid)){
                        user = users.get(i);
                    }
                }
                edName.getEditText().setText(user.getUserName());
                edPhone.getEditText().setText(user.getUserPhone());
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

    private int validateName(){
        int result = 1;
        String regName = "[A-Za-z ]*";

        if(edName.getEditText().getText().toString().trim().equals("")){
            edName.setError("Bạn chưa nhập họ tên người nhận hàng");
            result = 0;
        } else if(!edName.getEditText().getText().toString().trim().matches(regName)){
            edName.setError("Tên chỉ chứa ký tự chữ cái (a-z)");
            result = 0;
        } else {
            edName.setErrorEnabled(false);
        }
        return result;
    }

    private int validatePhone(){
        int result = 1;
        String regphone = "^(\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
        if(!edPhone.getEditText().getText().toString().trim().matches(regphone)){
            edPhone.setError("Số điện thoại gồm không đúng định dạng");
            result=0;
        } else if(edPhone.getEditText().getText().toString().trim().equals("")){
            edPhone.setError("Bạn chưa nhập số điện thoại nhận hàng");
            result=0;
        } else {
            edPhone.setErrorEnabled(false);
        }
        return result;
    }

    private int validateAddress(){
        int result = 1;
        String emailReg = "[A-Za-z]*\\w+@gmail.com";
        if(edAdress.getEditText().getText().toString().trim().equals("")){
            edAdress.setError("Bạn chưa nhập địa chỉ nhận hàng");
            result = 0;
        } else {
            edAdress.setErrorEnabled(false);
        }
        return result;
    }
}
