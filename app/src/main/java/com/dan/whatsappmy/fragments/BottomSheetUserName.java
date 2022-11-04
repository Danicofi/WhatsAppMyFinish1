package com.dan.whatsappmy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.Profile;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ImageProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;


public class BottomSheetUserName extends BottomSheetDialogFragment {

    UsersProvider mUserProvider;
    //FirebaseAuth mAuth;
    AuthProvider mAuth;
    Button buttonSave, buttonCancel;
    EditText editTextUserName;

    String username;

    public static BottomSheetUserName newInstance(String username){
        BottomSheetUserName bottomSheetSelectImage = new BottomSheetUserName();
        Bundle args = new Bundle();
        args.putString("username", username);
        bottomSheetSelectImage.setArguments(args);
        return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("username");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.button_sheet_username, container, false);

        buttonSave = view.findViewById(R.id.btnSave);
        buttonCancel = view.findViewById(R.id.btnCancel);
        editTextUserName = view.findViewById(R.id.editTextUserName);
                
        mUserProvider = new UsersProvider();
        //mAuth = FirebaseAuth.getInstance();
        mAuth = new AuthProvider();

        editTextUserName.setText(username);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserName();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private void updateUserName() {
        String username = editTextUserName.getText().toString();
        if (!username.equals("")) {
            mUserProvider.updateUserName(mAuth.getIdAut(), username).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dismiss();
                    Toast.makeText(getContext(), "El nombre de usuario se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
