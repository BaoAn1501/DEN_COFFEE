package com.nhom7.den_cafe.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.cloudmessaging.CloudMessagingReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.adapter.MessageAdapter;
import com.nhom7.den_cafe.home.AMChatFragment;
import com.nhom7.den_cafe.home.UMChatFragment;
import com.nhom7.den_cafe.model.Chat;
import com.nhom7.den_cafe.model.User;
import com.nhom7.den_cafe.notification.Token;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageFragment extends Fragment {
    View view;
    ImageView ivAvatar, ivBack, ivSend;
    TextView tvUsername;
    List<User> userList = new ArrayList<>();
    List<Chat> chatList = new ArrayList<>();
    EditText edInput;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
    User chatUser;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();
    RecyclerView rcv;
    MessageAdapter adapter;
    int from;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivAvatar = view.findViewById(R.id.ivAvatarMessage);
        ivBack = view.findViewById(R.id.ivBackMessage);
        tvUsername = view.findViewById(R.id.tvUserNameMessage);
        ivSend = view.findViewById(R.id.ivSendMessage);
        edInput = view.findViewById(R.id.edInputMessage);
        rcv = view.findViewById(R.id.rcvMF);
        Bundle bundle = getArguments();
        chatUser = (User) bundle.getSerializable("user");
        from = bundle.getInt("from");
        getChatUser();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from==1){
                    loadFragment1(new AMChatFragment());
                } else {
                    loadFragment2(new UMChatFragment());
                }
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edInput.getText().toString().trim();
                if(!message.equals("")){
                    sendMessage(uid, chatUser.getUserId(), message);
                    edInput.setText("");
                } else {
                    Toast.makeText(getActivity(), "Bạn chưa nhập nội dung tin nhắn", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rcv.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager((getContext()));
        layoutManager.setStackFromEnd(true);
        rcv.setLayoutManager(layoutManager);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateToken(s);
            }
        });
    }

    private void getChatUser(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                int pos = 0;
                for(int i=0;i<userList.size();i++){
                    if(userList.get(i).getUserId().equals(chatUser.getUserId())){
                        pos = i;
                    }

                }
                tvUsername.setText(userList.get(pos).getUserName());
                Glide
                        .with(getContext())
                        .load(userList.get(pos).getUserImage())
                        .centerCrop()
                        .placeholder(R.drawable.logoicon)
                        .into(ivAvatar);
                readMessage(uid, chatUser.getUserId(), userList.get(pos).getUserImage());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        chatRef.push().setValue(hashMap);
    }

    private void readMessage(final String myid, final String userid, final String imageurl){
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        chatList.add(chat);
                    }
                    adapter = new MessageAdapter(getContext(), chatList, imageurl);
                    rcv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToken(String token){
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        tokenRef.child(uid).setValue(token1);
    }

    private void loadFragment2(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
        transacion.commit();
    }

    private void loadFragment1(Fragment fragment) {
        FragmentTransaction transacion = getActivity().getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_AdminMain,fragment);
        transacion.commit();
    }

}
