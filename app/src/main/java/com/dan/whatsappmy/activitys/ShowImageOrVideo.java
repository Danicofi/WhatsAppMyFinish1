package com.dan.whatsappmy.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.dan.whatsappmy.R;
import com.squareup.picasso.Picasso;

public class ShowImageOrVideo extends AppCompatActivity {

    ImageView imageViewBackShow, imageViewPictureShow, imageViewVideoShow;
    FrameLayout frameLayoutVideoShow;
    VideoView videoViewShow;
    View viewVideoShow;
    String mExtraUrl, mExtraType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_or_video);
        setStatusBarColor();

        imageViewBackShow = findViewById(R.id.imageViewBackShow);
        imageViewPictureShow = findViewById(R.id.imageViewPictureShow);
        imageViewVideoShow = findViewById(R.id.imageViewVideoShow);
        frameLayoutVideoShow = findViewById(R.id.frameLayoutVideoShow);
        videoViewShow = findViewById(R.id.videoViewShow);
        viewVideoShow = findViewById(R.id.viewVideoShow);


        mExtraUrl = getIntent().getStringExtra("url");
        mExtraType = getIntent().getStringExtra("type");

        if(mExtraType.equals("imagen")){
            frameLayoutVideoShow.setVisibility(View.GONE);
            imageViewPictureShow.setVisibility(View.VISIBLE);
            Picasso.with(ShowImageOrVideo.this).load(mExtraUrl).into(imageViewPictureShow);
        } else {
            frameLayoutVideoShow.setVisibility(View.VISIBLE);
            imageViewPictureShow.setVisibility(View.GONE);
            Uri uri = Uri.parse(mExtraUrl);
            videoViewShow.setVideoURI(uri);
        }

        frameLayoutVideoShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!videoViewShow.isPlaying()){
                    viewVideoShow.setVisibility(View.GONE);
                    imageViewVideoShow.setVisibility(View.GONE);
                    videoViewShow.start();
                } else {
                    viewVideoShow.setVisibility(View.VISIBLE);
                    imageViewVideoShow.setVisibility(View.VISIBLE);
                    videoViewShow.pause();
                }
            }
        });

        imageViewBackShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
            if(!videoViewShow.isPlaying()){
                videoViewShow.pause();
            }
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }
}