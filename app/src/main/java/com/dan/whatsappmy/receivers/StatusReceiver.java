package com.dan.whatsappmy.receivers;


import static com.dan.whatsappmy.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.channel.NotificationHelper;
import com.dan.whatsappmy.models.Message;
import com.dan.whatsappmy.providers.MessageProvider;
import com.dan.whatsappmy.providers.NotificationProvider;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatusReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //getMyImage(context, intent);
        updateStatus(context, intent);
    }

    private void updateStatus(Context context, Intent intent) {

        int id = intent.getExtras().getInt("idNotification");
        String messagesJSON = intent.getExtras().getString("messages");
        MessageProvider messagesProvider = new MessageProvider();

        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        for(Message m: messages) {
            messagesProvider.updateStatus(m.getId(), "VISTO");
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

    }






}
