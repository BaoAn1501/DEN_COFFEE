package com.nhom7.den_cafe.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

public class SignInFragment extends Fragment {
    View view;
    TextInputLayout edPhone;
    TextView tvForgot, tvOr;
    CardView cvSignIn;
    ImageView ivGoogle, ivFacebook;
    List<User> list = new ArrayList<>();
    ProgressDialog progressDialog;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user_list");
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
        getListUser();
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        createRequestGoogle();
        mCallbackManager = CallbackManager.Factory.create();
        cvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edPhone.getEditText().getText().toString();
                if(validatePhone()>0){
                    signInNormal(phone);
                }
            }
        });
        ivGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

    }

    private void init(){
        edPhone = view.findViewById(R.id.edPhone_SignIn);
        tvForgot = view.findViewById(R.id.tvForgot_SignIn);
        tvOr = view.findViewById(R.id.tvOr);
        cvSignIn = view.findViewById(R.id.cvSignIn);
        ivFacebook = view.findViewById(R.id.ivFacebook_SignIn);
        ivGoogle = view.findViewById(R.id.ivGoogle_SignIn);
    }
    public void animated(){
        edPhone.setAlpha(0);
        tvForgot.setAlpha(0);
        cvSignIn.setAlpha(0);
        tvOr.setAlpha(0);
        ivFacebook.setAlpha(0);
        ivGoogle.setAlpha(0);
        edPhone.setTranslationX(700);
        tvForgot.setTranslationX(700);
        cvSignIn.setTranslationX(700);
        tvOr.setTranslationX(700);
        ivFacebook.setTranslationY(300);
        ivGoogle.setTranslationY(300);
        edPhone.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(350).start();
        tvForgot.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        cvSignIn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(450).start();
        tvOr.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        ivGoogle.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        ivFacebook.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(700).start();
    }

    private void signInNormal(String phone) {

    }

    private int validatePhone(){
        int result = 1;
        String regphone = "^(\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
        
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
                if(currentUser.getEmail().equals("cafeden_vau")){
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

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createRequestGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(getActivity(), UserMainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    private void signInFaceBook() {
        LoginManager.getInstance().logInWithReadPermissions(getActivity(),
                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), "error"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(getActivity(), UserMainActivity.class));
                            Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


}
