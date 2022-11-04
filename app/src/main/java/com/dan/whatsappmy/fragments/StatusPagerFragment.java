package com.dan.whatsappmy.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.ConfirmImageSend;
import com.dan.whatsappmy.activitys.StatusConfirm;
import com.dan.whatsappmy.adapters.CardAdapter;

import java.io.File;

public class StatusPagerFragment extends Fragment {



    CardView mCardViewOptions;
    View view;
    ImageView mImageViewPicture, imageViewBack, imageViewSend;
    LinearLayout linearLayoutImagePager;
    EditText editTextComment;


    public StatusPagerFragment() {
        // Required empty public constructor
    }


      public static Fragment newInstance(int position, String imagePad, int size) {
        StatusPagerFragment fragment = new StatusPagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("size", size);
        args.putString("image", imagePad);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_status_pager, container, false);
        mCardViewOptions = view.findViewById(R.id.cardviewOptions);
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        mImageViewPicture = view.findViewById(R.id.imageViewPicture);
        imageViewBack = view.findViewById(R.id.imageViewBack);
        linearLayoutImagePager = view.findViewById(R.id.linearLayoutViewPager);
        editTextComment =view.findViewById(R.id.editTextComment);
        imageViewSend = view.findViewById(R.id.imageViewSend);

        String comment = editTextComment.getText().toString();
        String imagePad = getArguments().getString("image");
        int size = getArguments().getInt("size");
        int position = getArguments().getInt("position");

        if(size == 1){
            linearLayoutImagePager.setPadding(0,0,0,0);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imageViewBack.getLayoutParams();
            params.leftMargin = 10;
            params.topMargin = 35;
        }

        if(imagePad != null){
            File file = new File(imagePad);
            mImageViewPicture.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((StatusConfirm)getActivity()).setComment(position, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((StatusConfirm)getActivity()).send();
            }
        });

        return view;
    }

    public CardView getCardView(){
        return mCardViewOptions;
    }

    // requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);



}