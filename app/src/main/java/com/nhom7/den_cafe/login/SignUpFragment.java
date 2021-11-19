package com.nhom7.den_cafe.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.UserMainActivity;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SignUpFragment extends Fragment {
    View view;
    TextInputLayout edPhone, edEmail, edName;
    CardView cvSignUp;
    public static final String TAG = SignUpFragment.class.getName();
    private ProgressDialog progressDialog;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<User> list = new ArrayList<>();
    User mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        getListUser();
        validation();
        cvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmail()>0 && validatePhone()>0 && validateName()>0){
                    String name = edName.getEditText().getText().toString().trim();
                    String phone = edPhone.getEditText().getText().toString().trim();
                    String email = edEmail.getEditText().getText().toString().trim();
                    mUser = new User(name, phone, email);
                    onClickVerifyPhoneNumber();
                }
            }
        });
    }

    private void init(){
        edEmail = view.findViewById(R.id.edEmail_SignUp);
        edName = view.findViewById(R.id.edName_SignUp);
        edPhone = view.findViewById(R.id.edPhone_SignUp);
        cvSignUp = view.findViewById(R.id.cvSignUp);
    }

    private void validation(){
        edEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateName();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private int validateEmail(){
        int result = 1;
        String emailReg = "[A-Za-z]*\\w+@gmail.com";
        if(edEmail.getEditText().getText().toString().trim().equals("")){
            edEmail.setError("Email không được để trống");
            result = 0;
        } else if(!edEmail.getEditText().getText().toString().trim().matches(emailReg)){
            edEmail.setError("Email không đúng định dạng");
            result = 0;
        } else {
            edEmail.setErrorEnabled(false);
        }
        for(int i=0;i<list.size();i++){
            if(list.get(i).getUserEmail().equals(edEmail.getEditText().getText().toString().trim())){
                edEmail.setError("Email đã được đăng ký trước đó");
                result = 0;
            }
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
            edPhone.setError("Số điện thoại không được để trống");
            result=0;
        } else {
            edPhone.setErrorEnabled(false);
        }
        for(int i=0;i<list.size();i++){
            if(list.get(i).getUserPhone().equals(edPhone.getEditText().getText().toString().trim())){
                edPhone.setError("Số điện thoại đã được đăng ký trước đó");
                result = 0;
            }
        }
        return result;
    }
    private int validateName(){
        int result = 1;
        String regName = "[A-Za-z ]*";
        if(edName.getEditText().getText().toString().trim().equals("")){
            edName.setError("Tên không được để trống");
            result = 0;
        } else if(!edName.getEditText().getText().toString().trim().matches(regName)){
            edName.setError("Tên không chứa ký tự số");
            result=0;
        } else {
            edName.setErrorEnabled(false);
        }
        return result;
    }

    private void getListUser(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void onClickVerifyPhoneNumber() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mUser.getUserPhone())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                // This callback will be invoked in two situations:
                                // 1 - Instant verification. In some cases the phone number can be instantly
                                //     verified without needing to send or enter a verification code.
                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                //     detect the incoming verification SMS and perform verification without
                                //     user action.
                                Log.d(TAG, "onVerificationCompleted:" + credential);
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Toast.makeText(getActivity(), "Verification "+mUser.getUserPhone()+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                Log.d(TAG, "onCodeSent:" + verificationId);

                                // Save verification ID and resending token so we can use them later
                                startActivity(new Intent(getActivity(), OTPActivity.class).putExtra("user", mUser).putExtra("verifyid", verificationId));
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            mUser.setUserId(user.getUid());
//                            startActivity(new Intent(getActivity(), OTPActivity.class).putExtra("user", mUser));
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getActivity(), "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
