package com.dan.whatsappmy.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.Chat;
import com.dan.whatsappmy.activitys.ChatMulti;
import com.dan.whatsappmy.models.ChatModel;
import com.dan.whatsappmy.models.Message;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.MessageProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<ChatModel, ChatsAdapter.viewHolder> {

    Context context;
    //FirebaseAuth authProvider;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    MessageProvider messagesProvider;
    User user;
    ListenerRegistration listener;
    ListenerRegistration listenerLastMessage;

    public ChatsAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        //authProvider = FirebaseAuth.getInstance();
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        messagesProvider = new MessageProvider();
        user = new User();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull final ChatModel chat) {

        String idUser = "";

        idUser=chat.getId();

        for (int i = 0; i < chat.getIds().size(); i++) {
            if (!authProvider.getIdAut().equals(chat.getIds().get(i))) {
                idUser = chat.getIds().get(i);
                break;
            }
        }

        getLastMessage(holder, chat.getId());

        if(chat.isMultiChat()){
            getMultiChatInfo(holder,chat);
        } else{
            getUserInfo(holder, idUser);
        }


        getMessagesNotRead(holder, chat.getId());


        clickMyView(holder, chat, idUser);
    }

    private void getMultiChatInfo(viewHolder holder, ChatModel chat) {
        if(chat.getImageGroup() != null){
            if(!chat.getImageGroup().equals("")){
                Picasso.with(context).load(chat.getImageGroup()).into(holder.circleImageUser);
            }
        }

        holder.textViewUsername.setText(chat.getGroupChat());
    }

    private void getMessagesNotRead(final viewHolder holder, final String idChat) {
        messagesProvider.getReceiverMessagesNotRead(idChat, authProvider.getIdAut()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null) {
                    int size = querySnapshot.size();
                    if (size > 0) {
                        holder.frameLayoutMessagesNotRead.setVisibility(View.VISIBLE);
                        holder.textViewMessagesNotRead.setText(String.valueOf(size));
                        holder.textViewTimestamp.setTextColor(context.getResources().getColor(R.color.colorGreenAccent));
                    }
                    else {
                        holder.frameLayoutMessagesNotRead.setVisibility(View.GONE);
                        holder.textViewTimestamp.setTextColor(context.getResources().getColor(R.color.colorGris));
                    }
                }
            }
        });

    }

    private void getLastMessage(final viewHolder holder, String idChat) {
        listenerLastMessage = messagesProvider.getLastMessage(idChat).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null) {
                    int size = querySnapshot.size();
                    if (size > 0) {
                        Message message = querySnapshot.getDocuments().get(0).toObject(Message.class);
                        holder.textViewLastMessage.setText(message.getMessage());
                        holder.textViewTimestamp.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(), context));

                        if (message.getIdSender().equals(authProvider.getIdAut())) {
                            holder.imageViewCheck.setVisibility(View.VISIBLE);
                            if (message.getStatus().equals("ENVIADO")) {
                                holder.imageViewCheck.setImageResource(R.drawable.double_check_gray);
                            }
                            else if (message.getStatus().equals("VISTO")) {
                                holder.imageViewCheck.setImageResource(R.drawable.double_check_blue);
                            }
                        }
                        else {
                            holder.imageViewCheck.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void clickMyView(viewHolder holder, ChatModel chat, final String idUser) {
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chat.isMultiChat()){
                    goToChatMultiActivity(chat);
                } else{
                    goToChatActivity(chat.getId(), idUser);
                }
            }
        });
    }

    private void goToChatMultiActivity(ChatModel chat) {
        Intent intent = new Intent(context, ChatMulti.class);
        Gson gson = new Gson();
        String chatJSON = gson.toJson(chat);
        intent.putExtra("chat", chatJSON);
        context.startActivity(intent);
    }

    private void getUserInfo(final viewHolder holder, String idUser) {

        listener = usersProvider.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        holder.textViewUsername.setText(user.getUsername());
                        if (user.getImage() != null) {
                            if (!user.getImage().equals("")) {
                                Picasso.with(context).load(user.getImage()).into(holder.circleImageUser);
                            }
                            else {
                                holder.circleImageUser.setImageResource(R.drawable.ic_baseline_person_24);
                            }
                        }
                        else {
                            holder.circleImageUser.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    }
                }

            }
        });

    }

    public ListenerRegistration getListener() {
        return listener;
    }

    public ListenerRegistration getListenerLastMessage() {
        return listenerLastMessage;
    }

    private void goToChatActivity(String idChat, String idUser) {
        Intent intent = new Intent(context, Chat.class);
        intent.putExtra("idUser", idUser);
        intent.putExtra("idChat", idChat);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView textViewUsername;
        TextView textViewLastMessage;
        TextView textViewTimestamp;
        CircleImageView circleImageUser;
        ImageView imageViewCheck;
        FrameLayout frameLayoutMessagesNotRead;
        TextView textViewMessagesNotRead;

        View myView;

        public viewHolder(View view) {
            super(view);
            myView = view;
            textViewUsername = view.findViewById(R.id.textViewUserNameChat);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessage);
            textViewTimestamp = view.findViewById(R.id.textViewTimestampChat);
            circleImageUser = view.findViewById(R.id.circlerImageUser);
            imageViewCheck = view.findViewById(R.id.imageViewCheck);
            frameLayoutMessagesNotRead = view.findViewById(R.id.frameLyoutMessagesNotRead);
            textViewMessagesNotRead = view.findViewById(R.id.textViewMessagesNotRead);
        }

    }
}
