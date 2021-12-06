package com.nhom7.den_cafe.chat;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nhom7.den_cafe.R;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class FirebaseNotificationService extends FirebaseMessagingService {
    String uid = FirebaseAuth.getInstance().getUid();
    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        updateToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size()>0){
            Map<String, String> map = remoteMessage.getData();
            String title = map.get("title");
            String message = map.get("message");
            String userId = map.get("userId");
            String userImage = map.get("userImage");
            String chatId = map.get("chatId");
            createNotification(title, message, userId, userImage, chatId);
        }

    }
    private void updateToken(String token){
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("list_user").child(uid);
        tokenRef.child("token").setValue(token);
    }

    @SuppressLint("ResourceAsColor")
    private void createNotification(String title, String message, String userId, String userImage, String chatId){
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(1000));
        builder.setContentTitle(title)
                .setContentText(message)
                .setColor(R.color.Brown)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_baseline_email_24_red)
                .setSound(defaultSound);
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("userId", userId);
        intent.putExtra("userImage", userImage);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(85-65), builder.build());
    }
}
