package com.nhom7.den_cafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.home.AMPersonFragment;
import com.nhom7.den_cafe.home.AMChatFragment;
import com.nhom7.den_cafe.home.AMOrderFragment;
import com.nhom7.den_cafe.home.AMProductFragment;
import com.nhom7.den_cafe.home.AMAnalysisFragment;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AdminMainActivity extends AppCompatActivity {
    private MeowBottomNavigation bnv_Main;
    String uid = FirebaseAuth.getInstance().getUid();
    int fromac = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        bnv_Main = findViewById(R.id.bnv_AdminMain);
        bnv_Main.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_analytics_24));
        bnv_Main.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_receipt_long_24));
        bnv_Main.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_local_library_24));
        bnv_Main.add(new MeowBottomNavigation.Model(4,R.drawable.ic_baseline_chat_24));
        bnv_Main.add(new MeowBottomNavigation.Model(5,R.drawable.ic_baseline_person_24_red));
        fromac = getIntent().getIntExtra("fromac", 0);
        if(fromac==0){
            bnv_Main.show(3,true);
            replace(new AMProductFragment());
        } else {
            bnv_Main.show(4, true);
            replace(new AMChatFragment());
        }
        bnv_Main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        replace(new AMAnalysisFragment());
                        break;
                    case 2:
                        replace(new AMOrderFragment());
                        break;
                    case 3:
                        replace(new AMProductFragment());
                        break;
                    case 4:
                        replace(new AMChatFragment());
                        break;
                    case 5:
                        replace(new AMPersonFragment());
                        break;
                }
                return null;
            }
        });
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transacion = getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_AdminMain,fragment);
        transacion.commit();
    }

    private void status(boolean status){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        userRef.child(uid).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(false);
    }
}