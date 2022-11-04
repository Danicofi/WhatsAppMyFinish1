package com.dan.whatsappmy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;


public class BottomSheetSelectImage extends BottomSheetDialogFragment {

    LinearLayout deleteImage, selectImage;
    ImageProvider mImageProvider;
    UsersProvider mUserProvider;
    //FirebaseAuth mAuth;
    AuthProvider mAuth;

    String image;

    public static BottomSheetSelectImage newInstance(String url){
        BottomSheetSelectImage bottomSheetSelectImage = new BottomSheetSelectImage();
        Bundle args = new Bundle();
        args.putString("image", url);
        bottomSheetSelectImage.setArguments(args);
        return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = getArguments().getString("image");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.button_sheet_select_image, container, false);
        deleteImage = view.findViewById(R.id.layoutDeleteImage);
        selectImage = view.findViewById(R.id.layoutSelecImage);

        mImageProvider = new ImageProvider();
        mUserProvider = new UsersProvider();
        //mAuth = FirebaseAuth.getInstance();
        mAuth = new AuthProvider();

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
            }
        });


        return view;
    }

    private void updateImage() {
        ((Profile)getActivity()).startPix();
    }

    private void deleteImage() {
        mImageProvider.delete(image).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mUserProvider.updateImage(mAuth.getIdAut(), null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                //setImageDefault();
                                dismiss();
                                Toast.makeText(getContext(), "La imagen se elimino correctamente", Toast.LENGTH_LONG).show();
                            } else {
                                dismiss();
                                Toast.makeText(getContext(), "No se pudo eliminar la imagen", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No se pudo eliminar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setImageDefault(){
        ((Profile)getActivity()).setImageDefault();
    }

}
