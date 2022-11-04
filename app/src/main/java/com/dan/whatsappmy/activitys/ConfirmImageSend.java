package com.dan.whatsappmy.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.adapters.OptionsPagerAdapter;
import com.dan.whatsappmy.models.Message;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ChatsProvider;
import com.dan.whatsappmy.providers.ImageProvider;
import com.dan.whatsappmy.providers.MessageProvider;
import com.dan.whatsappmy.providers.NotificationProvider;
import com.dan.whatsappmy.utils.ExtensionFile;
import com.dan.whatsappmy.utils.ShadowTransformer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmImageSend extends AppCompatActivity {


    ViewPager mViewPager;
    String mExtraIdChat;
    String mExtraIdReceiver;
    String mExtraIdNotification;
    ArrayList<String> data;
    ArrayList<Message> messages = new ArrayList<>();

    User mExtraMyUser;
    User mExtraReceiverUser;

    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;
    NotificationProvider mNotificationProvider;
    ChatsProvider mChatProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image_send);
        setStatusBarColor();

        mViewPager = findViewById(R.id.viewPager);
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();
        mNotificationProvider = new NotificationProvider();

        data = getIntent().getStringArrayListExtra("data");
        mExtraIdChat = getIntent().getStringExtra("idChat");
        mExtraIdReceiver = getIntent().getStringExtra("idReceiver");
        mExtraIdNotification = getIntent().getStringExtra("idNotification");

        String myUser = getIntent().getStringExtra("myUser");
        String receiverUser = getIntent().getStringExtra("receiverUser");

        Gson gson = new Gson();
        mExtraMyUser = gson.fromJson(myUser, User.class);
        mExtraReceiverUser = gson.fromJson(receiverUser, User.class);


        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                Message m = new Message();
                m.setIdChat(mExtraIdChat);
                m.setIdSender(mAuthProvider.getIdAut());
                m.setIdReceiver(mExtraIdReceiver);
                m.setStatus("ENVIADO");
                m.setTimestamp(new Date().getTime());
                m.setUrl(data.get(i));

                if(ExtensionFile.isImageFile(data.get(i))){

                    m.setType("imagen");
                    m.setMessage("\uD83D\uDCF7imagen");
                } else if (ExtensionFile.isVideoFile(data.get(i))){
                    m.setType("video");
                    m.setMessage("\uD83C\uDFA5video");
                }

                messages.add(m);
            }
        }


        OptionsPagerAdapter pagerAdapter = new OptionsPagerAdapter(
                getApplicationContext(),
                getSupportFragmentManager(),
                dpToPixels(2, this),
                data
        );
        ShadowTransformer transformer = new ShadowTransformer(mViewPager, pagerAdapter);
        transformer.enableScaling(true);

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(false, transformer);

    }

    public void send() {
        mImageProvider.uploadMultiple(ConfirmImageSend.this, messages);

        final Message message = new Message();
        message.setIdChat(mExtraIdChat);
        message.setIdSender(mAuthProvider.getIdAut());
        message.setIdReceiver(mExtraIdReceiver);

        message.setMessage("\uD83D\uDCF7 Imagen");
        message.setStatus("ENVIADO");
        message.setType("texto");
        message.setTimestamp(new Date().getTime());
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);

        sendNotification(messages);
        finish();
    }

    private void sendNotification(ArrayList<Message> messages) {
        Map<String, String> data = new HashMap<>();
        data.put("title", "📷 Imagen");
        data.put("body", "");
        data.put("idNotification", String.valueOf(mExtraIdNotification));
        data.put("usernameReceiver", mExtraReceiverUser.getUsername());
        data.put("usernameSender", mExtraMyUser.getUsername());
        data.put("imageReceiver", mExtraReceiverUser.getImage());
        data.put("imageSender", mExtraMyUser.getImage());
        data.put("idChat", mExtraIdChat);
        data.put("idSender", mAuthProvider.getIdAut());
        data.put("idReceiver", mExtraIdReceiver);
        data.put("tokenSender", mExtraMyUser.getToken());
        data.put("tokenReceiver", mExtraReceiverUser.getToken());


        Gson gson = new Gson();
        String messagesJSON = gson.toJson(messages);
        data.put("messagesJSON", messagesJSON);

        List<String> tokens = new ArrayList<>();
        tokens.add(mExtraReceiverUser.getToken());

        mNotificationProvider.send(ConfirmImageSend.this, tokens, data);
    }

    public void setMessage(int position, String message) {
        if (message.equals("")) {
            message = "\uD83D\uDCF7imagen";
        }
        messages.get(position).setMessage(message);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }

    private void updateStatus(Message[] messages) {
        final MessageProvider messagesProvider = new MessageProvider();
        for (Message m: messages) {
            messagesProvider.getMessagesById(m.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Message myMessage = documentSnapshot.toObject(Message.class);

                        if (!myMessage.getStatus().equals("VISTO")) {
                            messagesProvider.updateStatus(myMessage.getId(), "RECIBIDO");
                        }
                    }
                }
            });
        }
    }

}