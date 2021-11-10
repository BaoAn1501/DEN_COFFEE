package com.nhom7.den_cafe.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.nhom7.den_cafe.R;

public class SignInFragment extends Fragment {
    View view;
    TextInputLayout edEmail, edPass;
    TextView tvForgot, tvOr;
    CardView cvSignIn;
    ImageView ivGoogle, ivFacebook;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        animated();

    }
    private void init(){
        edEmail = view.findViewById(R.id.edEmail_SignIn);
        edPass = view.findViewById(R.id.edPass_SignIn);
        tvForgot = view.findViewById(R.id.tvForgot_SignIn);
        tvOr = view.findViewById(R.id.tvOr);
        cvSignIn = view.findViewById(R.id.cvSignIn);
        ivFacebook = view.findViewById(R.id.ivFacebook_SignIn);
        ivGoogle = view.findViewById(R.id.ivGoogle_SignIn);
    }
    public void animated(){
        edEmail.setAlpha(0);
        edPass.setAlpha(0);
        tvForgot.setAlpha(0);
        cvSignIn.setAlpha(0);
        tvOr.setAlpha(0);
        ivFacebook.setAlpha(0);
        ivGoogle.setAlpha(0);
        edEmail.setTranslationX(700);
        edPass.setTranslationX(700);
        tvForgot.setTranslationX(700);
        cvSignIn.setTranslationX(700);
        tvOr.setTranslationX(700);
        ivFacebook.setTranslationY(300);
        ivGoogle.setTranslationY(300);
        edEmail.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        edPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(350).start();
        tvForgot.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        cvSignIn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(450).start();
        tvOr.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        ivGoogle.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        ivFacebook.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(700).start();
    }
}
