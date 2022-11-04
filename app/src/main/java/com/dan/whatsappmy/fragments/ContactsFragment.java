package com.dan.whatsappmy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.adapters.ContactsAdapter;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class ContactsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerViewContacts;

    ContactsAdapter mAdapter;
    UsersProvider mUsersProvider;
    //FirebaseAuth authContactFrag;
    AuthProvider authContactFrag;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_contacts, container, false);
        mRecyclerViewContacts = mView.findViewById(R.id.rvContacts);
        mUsersProvider = new UsersProvider();
        //authContactFrag = FirebaseAuth.getInstance();
        authContactFrag = new AuthProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewContacts.setLayoutManager(linearLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mUsersProvider.getAllUsersByName();
                FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

                mAdapter = new ContactsAdapter(options, getContext());
                mRecyclerViewContacts.setAdapter(mAdapter);
                mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

}