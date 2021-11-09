package com.nhom7.den_cafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhom7.den_cafe.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView ivLogo;
    ConstraintLayout ivBackground;
    TextView tv;
    Animation aLogo, aText, aBackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        aBackground = AnimationUtils.loadAnimation(this, R.anim.splash_background);
        ivBackground.setAnimation(aBackground);
        aLogo = AnimationUtils.loadAnimation(this, R.anim.splash_icon);
        ivLogo.setAnimation(aLogo);
        aText = AnimationUtils.loadAnimation(this, R.anim.splash_text);
        tv.setAnimation(aText);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 4000);
    }
    private void init(){
        ivLogo = findViewById(R.id.logo_splash);
        ivBackground = findViewById(R.id.bg_splash);
        tv = findViewById(R.id.txt_splash);
    }
}