package com.nhom7.den_cafe.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.login.LoginActivity;

import org.jetbrains.annotations.NotNull;

public class UMPersonFragment extends Fragment {
    View view;
    TextView tvName, tvPhone, tvEmail;
    CardView cvChangeEmail, cvChangePass, cvChangePhone, cvLogout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_users");
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_um_person, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser!=null){
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

            }
        });
    }

    private void init(){
        tvName = view.findViewById(R.id.tvName_UserInfo);
        tvEmail = view.findViewById(R.id.tvEmail_UserInfo);
        tvPhone = view.findViewById(R.id.tvPhone_UserInfo);
        cvChangePass = view.findViewById(R.id.changePass_UserInfo);
        cvChangeEmail = view.findViewById(R.id.changeEmail_UserInfo);
        cvChangePhone = view.findViewById(R.id.changePhone_UserInfo);
        cvLogout = view.findViewById(R.id.logout_UserInfo);
    }
}