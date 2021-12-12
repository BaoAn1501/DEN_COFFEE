package com.nhom7.den_cafe.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nhom7.den_cafe.AdminMainActivity;
import com.nhom7.den_cafe.R;
import com.nhom7.den_cafe.UserMainActivity;
import com.nhom7.den_cafe.adapter.MessageAdapter;
import com.nhom7.den_cafe.model.Chat;
import com.nhom7.den_cafe.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    ImageView ivAvatar, ivBack, ivSend;
    TextView tvUsername;
    List<User> userList = new ArrayList<>();
    List<Chat> chatList = new ArrayList<>();
    EditText edInput;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
    public static final String TAG = MessageActivity.class.getName();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();
    String userid;
    RecyclerView rcv;
    MessageAdapter adapter;
    ValueEventListener seenListener;
    int from;
    boolean notify = false;
    APIService apiService;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);
        ivAvatar = findViewById(R.id.ivAvatarMessage);
        ivBack = findViewById(R.id.ivBackMessage);
        tvUsername = findViewById(R.id.tvUserNameMessage);
        ivSend = findViewById(R.id.ivSendMessage);
        edInput = findViewById(R.id.edInputMessage);
        rcv = findViewById(R.id.rcvMF);
        from = getIntent().getIntExtra("from", 0);
        getListUser();
        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);
        userid = getIntent().getStringExtra("userid");
        getChatUser();
        seenMessage(userid);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from==1){
                    startActivity(new Intent(MessageActivity.this, AdminMainActivity.class).putExtra("fromac", 1));
                } else {
                    startActivity(new Intent(MessageActivity.this, UserMainActivity.class).putExtra("fromac", 2));
                }
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = edInput.getText().toString().trim();
                if(!message.equals("")){
                    sendMessage(uid, userid, message);
                    edInput.setText("");
                } else {
                    Toast.makeText(MessageActivity.this, "Bạn chưa nhập nội dung tin nhắn", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rcv.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager((MessageActivity.this));
        layoutManager.setStackFromEnd(true);
        rcv.setLayoutManager(layoutManager);
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
                    if(userList.get(i).getUserId().equals(userid)){
                        pos = i;
                    }

                }
                tvUsername.setText(userList.get(pos).getUserName());
                Glide
                        .with(getApplicationContext())
                        .load(userList.get(pos).getUserImage())
                        .centerCrop()
                        .placeholder(R.drawable.logoicon)
                        .into(ivAvatar);
                readMessage(uid, userid, userList.get(pos).getUserImage());
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
        hashMap.put("isseen", false);
        chatRef.push().setValue(hashMap);
        final String msg = message;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_user").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify){
                    sendNotification(receiver, user.getUserName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokenRef.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(uid, R.drawable.ic_baseline_email_24_red, username+": "+msg, "Tin nhắn mới", userid);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().success!=1){
                                    Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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
                    adapter = new MessageAdapter(MessageActivity.this, chatList, imageurl);
                    rcv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage(final String useruid){
        seenListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(uid) && chat.getSender().equals(useruid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void status(boolean status){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        userRef.child(uid).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatRef.removeEventListener(seenListener);
    }

    private void getListUser(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



}