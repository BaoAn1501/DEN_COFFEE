package com.nhom7.den_cafe.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.AdminMainActivity;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.UserMainActivity;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignInFragment extends Fragment {
    View view;
    TextInputLayout edPhone;
    CardView cvSignIn, cvHelp;
    List<User> list = new ArrayList<>();
    ProgressDialog progressDialog;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private static final String TAG = SignInFragment.class.getName();
    private static final int RC_SIGN_IN = 123;
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
//        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
//        createRequestGoogle();
        getListUser();
        validation();
//        mCallbackManager = CallbackManager.Factory.create();
        cvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edPhone.getEditText().getText().toString().trim();
                if(validatePhone()>0){
                    VerifyPhoneNumber(phone);
                }
            }
        });
        cvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogHelper();
            }
        });
    }

    private void openDialogHelper(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.dialog_alert);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_helper, null);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view);
        dialog.show();
    }

    private void init(){
        edPhone = view.findViewById(R.id.edPhone_SignIn);
        cvSignIn = view.findViewById(R.id.cvSignIn);
        cvHelp = view.findViewById(R.id.cvHelpSignIn);
    }
    public void animated(){
        edPhone.setAlpha(0);
        cvSignIn.setAlpha(0);
        cvHelp.setAlpha(0);
        edPhone.setTranslationX(700);
        cvSignIn.setTranslationX(700);
        cvHelp.setTranslationX(700);
        edPhone.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(350).start();
        cvHelp.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        cvSignIn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(450).start();
    }

    private int validateInput(){
        int result = 1;
        String regphone = "^(\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
        if(edPhone.getEditText().getText().toString().trim().equals("")){
            edPhone.setError("Số điện thoại không được để trống");
            result=0;
        } else if(!edPhone.getEditText().getText().toString().trim().matches(regphone)){
            edPhone.setError("Số điện thoại không đúng định dạng");
            result=0;
        }
        else {
            edPhone.setErrorEnabled(false);
        }
        return result;
    }
    private int validatePhone(){
        int result = 1;
        int getphone = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getUserPhone().equals(edPhone.getEditText().getText().toString().trim())){
                getphone++;
            }
        }
        if(getphone==0){
            edPhone.setError("Số điện thoại chưa được đăng ký");
            result = 0;
        } else {
            edPhone.setErrorEnabled(false);
        }
        if(edPhone.getEditText().getText().toString().trim().equals("+84387463895")){
            result=1;
            edPhone.setErrorEnabled(false);
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    public void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            try {
                if(currentUser.getPhoneNumber().equals("+84387463895")){
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
                    userRef.child(currentUser.getUid()).setValue(new User(currentUser.getUid(), "DEN CAFE", currentUser.getPhoneNumber(), "dencafe@gmail.com", "default"));
                    startActivity(new Intent(getActivity(), AdminMainActivity.class));
                }else {
                    startActivity(new Intent(getActivity(), UserMainActivity.class));
                }
            } catch (Exception ex){

            }
        }else {
            Toast.makeText(getContext(), "Mời đăng nhập để tiếp tục.", Toast.LENGTH_SHORT).show();
        }
    }

//    private void signInGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    private void createRequestGoogle() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//            }
//        }
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                            startActivity(new Intent(getActivity(), UserMainActivity.class));
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
//                        }
//                    }
//                });
//    }
//    private void signInFaceBook() {
//        LoginManager.getInstance().logInWithReadPermissions(getActivity(),
//                Arrays.asList("email", "public_profile"));
//        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Toast.makeText(getActivity(), "error"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                            startActivity(new Intent(getActivity(), UserMainActivity.class));
//                            Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(getActivity(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }

    private void VerifyPhoneNumber(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
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
                                Toast.makeText(getActivity(), "Verification "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                Log.d(TAG, "onCodeSent:" + verificationId);

                                // Save verification ID and resending token so we can use them later
                                startActivity(new Intent(getActivity(), OTPSignInActivity.class).putExtra("phone", phone).putExtra("verifyid", verificationId));
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
//                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
//                            userRef.child(user.getUid()).setValue(new User(user.getUid(), "DEN CAFE", user.getPhoneNumber(), "dencafe@gmail.com", "default"));
                            // Update UI
                            updateUI(user);
                            startActivity(new Intent(getActivity(), UserMainActivity.class));
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

    private void validation(){
        edPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
