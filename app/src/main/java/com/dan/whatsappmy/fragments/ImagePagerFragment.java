package com.dan.whatsappmy.fragments;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.ConfirmImageSend;
import com.dan.whatsappmy.adapters.CardAdapter;
import com.dan.whatsappmy.utils.ExtensionFile;

import java.io.File;

public class ImagePagerFragment extends Fragment {

    View mView;
    CardView mCardViewOptions;
    ImageView mImageViewPicture;
    ImageView mImageViewBack;
    ImageView mImageViewSend;
    ImageView mImageViewVideo;
    LinearLayout mLinearLayoutImagePager;
    EditText mEditTextComment;
    FrameLayout mFrameLayoutVideo;
    VideoView mVideo;
    View mViewVideo;



    public static Fragment newInstance(int position, String imagePath, int size) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("size", size);
        args.putString("image", imagePath);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_image_pager, container, false);
        mCardViewOptions = mView.findViewById(R.id.cardviewOptions);
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        mImageViewPicture = mView.findViewById(R.id.imageViewPicture);
        mImageViewBack = mView.findViewById(R.id.imageViewBack);
        mImageViewSend = mView.findViewById(R.id.imageViewSend);
        mLinearLayoutImagePager = mView.findViewById(R.id.linearLayoutViewPager);
        mEditTextComment = mView.findViewById(R.id.editTextComment);
        mFrameLayoutVideo = mView.findViewById(R.id.frameLayoutVideo);
        mVideo = mView.findViewById(R.id.videoView);
        mImageViewVideo = mView.findViewById(R.id.imageViewVideo);
        mViewVideo = mView.findViewById(R.id.viewVideo);

        String imagePath = getArguments().getString("image");
        int size = getArguments().getInt("size");
        final int position = getArguments().getInt("position");

        if(ExtensionFile.isImageFile(imagePath)){
            mFrameLayoutVideo.setVisibility(View.GONE);
            mImageViewPicture.setVisibility(View.VISIBLE);
            if (imagePath != null) {
                File file = new File(imagePath);
                mImageViewPicture.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        }else if(ExtensionFile.isVideoFile(imagePath)){
            mFrameLayoutVideo.setVisibility(View.VISIBLE);
            mImageViewPicture.setVisibility(View.GONE);
            Uri uri = Uri.parse(imagePath);
            mVideo.setVideoURI(uri);
        }

        if (size == 1) {
            mLinearLayoutImagePager.setPadding(0, 0,0 ,0);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mImageViewBack.getLayoutParams();
            params.leftMargin = 10;
            params.topMargin = 35;
        }



        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        mEditTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((ConfirmImageSend) getActivity()).setMessage(position, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ConfirmImageSend) getActivity()).send();
            }
        });
        mFrameLayoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mVideo.isPlaying()){
                    mViewVideo.setVisibility(View.GONE);
                    mImageViewVideo.setVisibility(View.GONE);
                    mVideo.start();
                } else {
                    mViewVideo.setVisibility(View.VISIBLE);
                    mImageViewVideo.setVisibility(View.VISIBLE);
                    mVideo.pause();
                }
            }
        });

        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mVideo.isPlaying()){
            mVideo.pause();
        }
    }

    public CardView getCardView() {
        return mCardViewOptions;
    }
    // requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);



}