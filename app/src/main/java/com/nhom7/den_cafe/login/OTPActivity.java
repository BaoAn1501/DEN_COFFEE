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
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.UserMainActivity;
import com.nhom7.den_cafe.model.User;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    TextInputLayout edCode;
    CardView cvContinue;
    TextView tvCount, tvResend;
    User mUser;
    private String verifyid;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private static final String TAG = OTPActivity.class.getName();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        init();
        updateTime();
        Toast.makeText(this, "mUser name: "+mUser.getUserName(), Toast.LENGTH_SHORT).show();
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendAgain();
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
        edCode = findViewById(R.id.edEnterOTP);
        cvContinue = findViewById(R.id.cvContinueOTP);
        tvCount = findViewById(R.id.countTimeOTP);
        tvResend = findViewById(R.id.resendOTP);
        getDataIntent();
    }
    private void updateTime(){
        long duration = TimeUnit.MINUTES.toMillis(1);
        new CountDownTimer(duration, 1000) {
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
                Toast.makeText(OTPActivity.this, "Hết hạn xác nhận mã OTP, vui lòng bấm gửi lại mã OTP", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
    private void getDataIntent(){
        mUser = (User) getIntent().getSerializableExtra("user");
        verifyid = getIntent().getStringExtra("verifyid");
    }

    private void onClickSendOtp(String strOtp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyid, strOtp);
        signInWithPhoneAuthCredential(credential);
    }

    private void onClickSendAgain() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mUser.getUserPhone())       // Phone number to verify
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
                                Toast.makeText(OTPActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
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
                            mUser.setUserId(user.getUid());
                            userRef.child(mUser.getUserId()).setValue(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(OTPActivity.this, "Thêm người dùng thànhc công", Toast.LENGTH_SHORT).show();
                                }
                            });
                            startActivity(new Intent(OTPActivity.this, UserMainActivity.class).putExtra("user", mUser));
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OTPActivity.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}