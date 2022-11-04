package com.dan.whatsappmy.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.StatusDetail;
import com.dan.whatsappmy.adapters.StatusViewersAdapter;
import com.dan.whatsappmy.models.StatusViewer;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.StatusViewerProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.Query;


public class BottomSheetViewers extends BottomSheetDialogFragment {

    RecyclerView mRecyclerView;
    StatusViewersAdapter mAdapter;

    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    StatusViewerProvider mStatusViewerProvider;


    String idStory;

    public static BottomSheetViewers newInstance(String idStory) {
        BottomSheetViewers bottomSheetSelectImage = new BottomSheetViewers();
        Bundle args = new Bundle();
        args.putString("idStory", idStory);
        bottomSheetSelectImage.setArguments(args);
        return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idStory = getArguments().getString("idStory");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_viewers, container, false);
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mStatusViewerProvider = new StatusViewerProvider();

        mRecyclerView = view.findViewById(R.id.rvStatusViewers);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ((StatusDetail) getActivity()).pause();
            }
        });

        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        ((StatusDetail) getActivity()).start();
        super.onCancel(dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mStatusViewerProvider.getStatusViewersByIdStatus(idStory);

        FirestoreRecyclerOptions<StatusViewer> options = new FirestoreRecyclerOptions.Builder<StatusViewer>()
                .setQuery(query, StatusViewer.class)
                .build();

        mAdapter = new StatusViewersAdapter(options, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
