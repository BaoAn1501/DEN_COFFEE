package com.nhom7.den_cafe.home;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.SearchProductAdapter;
import com.nhom7.den_cafe.adapter.UserListChatAdapter;
import com.nhom7.den_cafe.chat.Token;
import com.nhom7.den_cafe.model.Product;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UMChatFragment extends Fragment {
    View view;
    AutoCompleteTextView autoComplete;
    RecyclerView rcv;
    UserListChatAdapter adapter;
    List<User> userList = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getUid();
    String token;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_um_chat, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoComplete = view.findViewById(R.id.autoChatUser);
        rcv = view.findViewById(R.id.rcvChatUser);
        getAllUser();
        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().equals("")){
                    getAllUser();
                } else {
                    getListAuto(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateToken(s);
            }
        });
    }

    private void saveToken(String token) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("tokens");
        tokenRef.child(uid).setValue(token);
    }

    private void getListAuto(CharSequence s) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("list_user");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getUserName().contains(s) && !user.getUserId().equals(uid)){
                        userList.add(user);
                    } else {

                    }
                }
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                rcv.setLayoutManager(manager);
                adapter = new UserListChatAdapter(getContext(), userList);
                rcv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getAllUser() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("list_user");
        List<User> userList = new ArrayList<>();
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.getUserId().equals(uid)){
                        userList.add(user);
                    }
                }
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                rcv.setLayoutManager(manager);
                adapter = new UserListChatAdapter(getContext(), userList);
                rcv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void updateToken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        databaseReference.child(uid).setValue(token1);
    }
}