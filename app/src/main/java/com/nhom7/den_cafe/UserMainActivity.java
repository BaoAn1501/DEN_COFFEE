package com.nhom7.den_cafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.nhom7.den_cafe.home.AMOrderFragment;
import com.nhom7.den_cafe.home.UMAnalysisFragment;
import com.nhom7.den_cafe.home.UMChatFragment;
import com.nhom7.den_cafe.home.UMOrderFragment;
import com.nhom7.den_cafe.home.UMPersonFragment;
import com.nhom7.den_cafe.home.UMProductFragment;
import com.nhom7.den_cafe.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class UserMainActivity extends AppCompatActivity {

    private MeowBottomNavigation bnv_Main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        bnv_Main = findViewById(R.id.bnv_UserMain);
        User user = (User) getIntent().getSerializableExtra("user");
        Toast.makeText(this, "User name"+user.getUserName(), Toast.LENGTH_SHORT).show();
        bnv_Main.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_analytics_24));
        bnv_Main.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_receipt_long_24));
        bnv_Main.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_local_library_24));
        bnv_Main.add(new MeowBottomNavigation.Model(4,R.drawable.ic_baseline_chat_24));
        bnv_Main.add(new MeowBottomNavigation.Model(5,R.drawable.ic_baseline_person_24));
        bnv_Main.show(3,true);
        replace(new UMProductFragment());
        bnv_Main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        replace(new UMOrderFragment());
                        break;
                    case 2:
                        replace(new UMAnalysisFragment());
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
}