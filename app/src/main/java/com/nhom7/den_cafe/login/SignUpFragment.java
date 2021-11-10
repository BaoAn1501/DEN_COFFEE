package com.nhom7.den_cafe.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.nhom7.den_cafe.R;

public class SignUpFragment extends Fragment {
    View view;
    TextInputLayout edEmail, edPass, edConfirmPass;
    CardView cvSignUp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        animated();

    }
    private void init(){
        edEmail = view.findViewById(R.id.edEmail_SignUp);
        edPass = view.findViewById(R.id.edPass_SignUp);
        edConfirmPass = view.findViewById(R.id.edEmail_SignUp);
        cvSignUp = view.findViewById(R.id.cvSignUp);
    }
    public void animated(){
        edEmail.setAlpha(0);
        edPass.setAlpha(0);
        edConfirmPass.setAlpha(0);
        cvSignUp.setAlpha(0);
        edEmail.setTranslationX(700);
        edPass.setTranslationX(700);
        edConfirmPass.setTranslationX(700);
        cvSignUp.setTranslationX(700);
        edEmail.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        edPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(350).start();
        edConfirmPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        cvSignUp.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(450).start();
    }
}
