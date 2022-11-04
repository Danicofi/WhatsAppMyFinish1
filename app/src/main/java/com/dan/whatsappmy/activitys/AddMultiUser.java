package com.dan.whatsappmy.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.adapters.MultiUsersAdapter;
import com.dan.whatsappmy.models.ChatModel;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.MyToolBar;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class AddMultiUser extends AppCompatActivity {


    RecyclerView mRecyclerViewContacts;

    MultiUsersAdapter mAdapter;
    UsersProvider mUsersProvider;
    AuthProvider mAuthprovider;
    FloatingActionButton mFabCheck;
    ArrayList<User> mUsersSelected;
    String idAuth;
    Menu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_multi_user);

        MyToolBar.show(AddMultiUser.this, "Añadir Grupo", true);

        mRecyclerViewContacts = findViewById(R.id.rvContactsAddMultiUser);
        mUsersProvider = new UsersProvider();
        mAuthprovider = new AuthProvider();

        mFabCheck = findViewById(R.id.fabCheck);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddMultiUser.this);
        mRecyclerViewContacts.setLayoutManager(linearLayoutManager);


        mFabCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUsersSelected != null){
                    if(mUsersSelected.size() >= 2){
                        createChatGroup();
                    } else {
                        Toast.makeText(AddMultiUser.this, "Por favor selecciona al menos 2 ususarios", Toast.LENGTH_SHORT).show();
                    }

            } else {
                    Toast.makeText(AddMultiUser.this, "Por favor selecciona al menos 2 ususarios", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void setUsers(ArrayList<User> users){
       if(mMenu != null){
        mUsersSelected = users;
           if(users.size() > 0){
               mMenu.findItem(R.id.itemCount).setTitle(Html.fromHtml("<font color='#ffffff'>" + users.size() + "</font>"));
           }else{
           mMenu.findItem(R.id.itemCount).setTitle(Html.fromHtml(""));
       }
       }
    }

    private void createChatGroup(){
        Random random = new Random();
        int n = random.nextInt(100000);
        ChatModel chat = new ChatModel();
        chat.setId(UUID.randomUUID().toString());
        chat.setTimestamp(new Date().getTime());
        chat.setNumberMessages(1);
        chat.setWriting("");
        chat.setIdNotification(n);
        chat.setMultiChat(true);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthprovider.getIdAut());



        for(User u : mUsersSelected){
            ids.add(u.getId());
        }

        chat.setIds(ids);
        Gson gson = new Gson();
        String chatJSON = gson.toJson(chat);

        Intent i = new Intent(AddMultiUser.this, ConfirmMultiChat.class);
        i.putExtra("chat", chatJSON);
        startActivity(i);
    }


    @Override
    public void onStart() {
        super.onStart();
        Query query = mUsersProvider.getAllUsersByName();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        mAdapter = new MultiUsersAdapter(options, AddMultiUser.this);
        mRecyclerViewContacts.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_user_menu, menu);
        mMenu=menu;

        return true;
    }
}