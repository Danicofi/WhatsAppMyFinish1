package com.dan.whatsappmy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.Chat;
import com.dan.whatsappmy.adapters.ChatsAdapter;
import com.dan.whatsappmy.adapters.ContactsAdapter;
import com.dan.whatsappmy.adapters.MessagesAdapter;
import com.dan.whatsappmy.models.ChatModel;
import com.dan.whatsappmy.models.Message;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ChatsProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class ChatsFragment extends Fragment {
    View mView;
    RecyclerView mRecyclerViewChats;

    ChatsAdapter mAdapter;

    UsersProvider mUsersProvider;
    //FirebaseAuth authChatsFrag;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;


    public ChatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chats, container, false);
        mRecyclerViewChats = mView.findViewById(R.id.rvChats);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewChats.setLayoutManager(linearLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mChatsProvider.getUserChats(mAuthProvider.getIdAut());

        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>()
                .setQuery(query, ChatModel.class)
                .build();

        mAdapter = new ChatsAdapter(options, getContext());
        mRecyclerViewChats.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("ENTRO", "ON STOP SE DETUVO");
        mAdapter.stopListening();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter.getListener() != null) {
            mAdapter.getListener().remove();
        }

        if (mAdapter.getListenerLastMessage() != null) {
            mAdapter.getListenerLastMessage().remove();
        }
    }
}