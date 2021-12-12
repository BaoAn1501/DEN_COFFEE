package com.nhom7.den_cafe.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.UserListChatAMAdapter;
import com.nhom7.den_cafe.adapter.UserListChatAdapter;
import com.nhom7.den_cafe.chat.Token;
import com.nhom7.den_cafe.model.Chat;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AMChatFragment extends Fragment {
    View view;
    RecyclerView rcv;
    UserListChatAMAdapter adapter;
    List<User> mUsers = new ArrayList<>();
    List<String> usersList = new ArrayList<>();
    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    String myUid = FirebaseAuth.getInstance().getUid();
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_am_chat, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvAMCF);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getSender().equals(myUid)){
                        usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(myUid)){
                        usersList.add(chat.getSender());
                    }
                }
                readChats();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateToken(s);
            }
        });
    }

    private void readChats() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                //get Chilren sua lai get GetUid
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    //Display 1 user form chat
//                    String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    for(String id:usersList){
                        if(user.getUserId().equals(id)){
                            if(mUsers.size()!=0){
                                for(User user1:mUsers){
                                    if(!user.getUserId().equals(user1.getUserId())){
                                        mUsers.add(user);
                                    }
                                }
                            }else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                adapter=new UserListChatAMAdapter(getContext(),mUsers);
                rcv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        databaseReference.child(myUid).setValue(token1);
    }
}