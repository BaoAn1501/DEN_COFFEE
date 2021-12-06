package com.nhom7.den_cafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.home.AMChatFragment;
import com.nhom7.den_cafe.home.AMProductFragment;
import com.nhom7.den_cafe.home.UMAnalysisFragment;
import com.nhom7.den_cafe.home.UMChatFragment;
import com.nhom7.den_cafe.home.UMOrderFragment;
import com.nhom7.den_cafe.home.UMPersonFragment;
import com.nhom7.den_cafe.home.UMProductFragment;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class UserMainActivity extends AppCompatActivity {
    private MeowBottomNavigation bnv_Main;
    String uid = FirebaseAuth.getInstance().getUid();
    int fromac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        bnv_Main = findViewById(R.id.bnv_UserMain);
        bnv_Main.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_analytics_24));
        bnv_Main.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_receipt_long_24));
        bnv_Main.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_local_library_24));
        bnv_Main.add(new MeowBottomNavigation.Model(4,R.drawable.ic_baseline_chat_24));
        bnv_Main.add(new MeowBottomNavigation.Model(5,R.drawable.ic_baseline_person_24_red));
        fromac = getIntent().getIntExtra("fromac",0);
        if(fromac==0){
            bnv_Main.show(3,true);
            replace(new UMProductFragment());
        } else {
            bnv_Main.show(4, true);
            replace(new UMChatFragment());
        }
        bnv_Main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        replace(new UMAnalysisFragment());
                        break;
                    case 2:
                        replace(new UMOrderFragment());
                        break;
                    case 3:
                        replace(new UMProductFragment());
                        break;
                    case 4:
                        replace(new UMChatFragment());
                        break;
                    case 5:
                        replace(new UMPersonFragment());
                        break;
                }
                return null;
            }
        });
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transacion = getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
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