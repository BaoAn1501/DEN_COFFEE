package com.nhom7.den_cafe.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom7.den_cafe.AdminMainActivity;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.UserMainActivity;
import com.nhom7.den_cafe.model.User;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OTPSignInActivity extends AppCompatActivity {

    TextInputLayout edCode;
    CardView cvContinue;
    TextView tvCount, tvResend;
    User mUser;
    private String verifyid;
    String phonenumber;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private static final String TAG = OTPActivity.class.getName();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CountDownTimer count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpsign_in);
        init();
        updateTime();

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendAgain();
                count.cancel();
                updateTime();
            }
        });
        cvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strOtp = edCode.getEditText().getText().toString().trim();
                onClickSendOtp(strOtp);
            }
        });
    }
    private void init(){
        edCode = findViewById(R.id.edEnterOTPSignIn);
        cvContinue = findViewById(R.id.cvContinueOTPSignIn);
        tvCount = findViewById(R.id.countTimeOTPSignIn);
        tvResend = findViewById(R.id.resendOTPSignIn);
        getDataIntent();
    }
    private void updateTime(){
        long duration = TimeUnit.MINUTES.toMillis(1);
        count = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                tvCount.setText(sDuration);
            }

            @Override
            public void onFinish() {
                tvCount.setVisibility(View.GONE);
            }
        }.start();
    }

    private void getDataIntent(){
        verifyid = getIntent().getStringExtra("verifyid");
        phonenumber = getIntent().getStringExtra("phone");
    }

    private void onClickSendOtp(String strOtp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyid, strOtp);
        signInWithPhoneAuthCredential(credential);
    }

    private void onClickSendAgain() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setForceResendingToken(mToken)
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
                                Toast.makeText(OTPSignInActivity.this, "Verification Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                Log.d(TAG, "onCodeSent:" + verificationId);

                                // Save verification ID and resending token so we can use them later
                                verifyid = verificationId;
                                mToken = token;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            if(phonenumber.equals("+84387463895")){
                                startActivity(new Intent(OTPSignInActivity.this, AdminMainActivity.class).putExtra("phone", phonenumber));
                            } else{
                                startActivity(new Intent(OTPSignInActivity.this, UserMainActivity.class).putExtra("phone", phonenumber));
                            }

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OTPSignInActivity.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}