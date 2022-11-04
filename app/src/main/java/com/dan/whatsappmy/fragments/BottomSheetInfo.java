package com.dan.whatsappmy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;


public class BottomSheetInfo extends BottomSheetDialogFragment {

    UsersProvider mUserProvider;
    //FirebaseAuth mAuth;
    AuthProvider mAuth;
    Button buttonSave, buttonCancel;
    EditText editTextInfo;

    String info;

    public static BottomSheetInfo newInstance(String info){
        BottomSheetInfo bottomSheetSelectImage = new BottomSheetInfo();
        Bundle args = new Bundle();
        args.putString("info", info);
        bottomSheetSelectImage.setArguments(args);
        return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getString("info");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.button_sheet_info, container, false);

        buttonSave = view.findViewById(R.id.btnSave);
        buttonCancel = view.findViewById(R.id.btnCancel);
        editTextInfo = view.findViewById(R.id.editTextInfo);
                
        mUserProvider = new UsersProvider();
        //mAuth = FirebaseAuth.getInstance();
        mAuth = new AuthProvider();

        editTextInfo.setText(info);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
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

    private void updateInfo() {
        String info = editTextInfo.getText().toString();
        if (!info.equals("")) {
            mUserProvider.updateInfo(mAuth.getIdAut(), info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dismiss();
                    Toast.makeText(getContext(), "El estado se ha actualizado correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
