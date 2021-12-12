package com.nhom7.den_cafe.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.login.LoginActivity;
import com.nhom7.den_cafe.model.ProductType;
import com.nhom7.den_cafe.model.User;
import com.nhom7.den_cafe.product.AddProductFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UMPersonFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 111;
    View view;
    TextView tvName, tvPhone, tvEmail;
    ImageView ivProfile;
    CardView cvLogout, cvSetAvatar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();
    private StorageTask storageTask;
    ProgressDialog progressDialog;
    private List<User> list = new ArrayList<>();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("imageFolder");
    private Uri mImageUri;
    User mUser;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_um_person, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
//        showUserInfo();
        getUserInfo();
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser!=null){
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
    }

    private void init(){
        tvName = view.findViewById(R.id.tvName_UserInfo);
        tvEmail = view.findViewById(R.id.tvEmail_UserInfo);
        tvPhone = view.findViewById(R.id.tvPhone_UserInfo);
        ivProfile = view.findViewById(R.id.ivProfile_UserInfo);
        cvLogout = view.findViewById(R.id.logout_UserInfo);
    }

    private void getUserInfo(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                int pos = 0;
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getUserId().equals(uid)){
                        pos = i;
                    }
                }
                mUser = list.get(pos);
                tvName.setText(mUser.getUserName());
                tvPhone.setText(mUser.getUserPhone());
                tvEmail.setText(mUser.getUserEmail());
                if(mUser.getUserImage().equals("default")){
                    ivProfile.setImageResource(R.drawable.logoicon);
                } else {
                    Glide
                            .with(getContext())
                            .load(mUser.getUserImage())
                            .centerCrop()
                            .placeholder(R.drawable.logoicon)
                            .into(ivProfile);
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            ivProfile.setImageURI(mImageUri);
            Glide.with(this).load(mImageUri).into(ivProfile);
            if(mImageUri!=null){
                chaneImageProfile();
            }
        }
    }

    private void chaneImageProfile() {
        if (mImageUri != null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Đang xử lý...");
            progressDialog.show();
            String key = UUID.randomUUID().toString();
            StorageReference fileReference = storageRef.child("imageProfile/" + key);
            storageTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();
                            String imageUrl = url.toString();
                            userRef.child(mUser.getUserId()).child("userImage").setValue(imageUrl);
                            progressDialog.dismiss();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Bạn chưa chọn ảnh cho loại món", Toast.LENGTH_SHORT).show();
        }
    }
}