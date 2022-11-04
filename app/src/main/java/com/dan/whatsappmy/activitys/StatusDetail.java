package com.dan.whatsappmy.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.fragments.BottomSheetViewers;
import com.dan.whatsappmy.models.Status;
import com.dan.whatsappmy.models.StatusViewer;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.StatusProvider;
import com.dan.whatsappmy.providers.StatusViewerProvider;
import com.google.gson.Gson;

import java.net.URL;
import java.util.Date;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StatusDetail extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    StoriesProgressView mStoriesProgressView;
    TextView mTextViewComment;
    ImageView mImageViewStatus;
    View mView;

    Status[] mStatus;
    Gson mGson = new Gson();

    GestureDetector mDetector;
    BottomSheetViewers mBottomSheetViewers;

    int mCounter = 0;

    AuthProvider mAuthProvider;
    StatusViewerProvider mStatusViewerProvider;

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mStoriesProgressView = findViewById(R.id.storiesProgressView);
        mTextViewComment = findViewById(R.id.textViewComment);
        mImageViewStatus = findViewById(R.id.imageViewStatus);
        mView = findViewById(R.id.mainView);

        mAuthProvider = new AuthProvider();
        mStatusViewerProvider = new StatusViewerProvider();
        mBottomSheetViewers = new BottomSheetViewers();

        mCounter = getIntent().getIntExtra("counter", 0);

        mDetector = new GestureDetector(this, new MyGestureListener());
        mStoriesProgressView.setStoriesListener(this);

        String statusJSON = getIntent().getStringExtra("status");
        mStatus = mGson.fromJson(statusJSON, Status[].class);

        mStoriesProgressView.setStoriesCount(mStatus.length);
        mStoriesProgressView.setStoryDuration(4000);
        mStoriesProgressView.startStories(mCounter);
        setStatusInfo();

        setStatusBarColor();

        mView.setOnTouchListener(onTouchListener);
    }

    private void updateStatus(Status status) {
        StatusViewer statusViewer = new StatusViewer();
        statusViewer.setIdStatus(status.getId());
        statusViewer.setIdUser(mAuthProvider.getIdAut());
        statusViewer.setTimestamp(new Date().getTime());
        statusViewer.setId(mAuthProvider.getIdAut() + status.getId());
        mStatusViewerProvider.create(statusViewer);
    }

    private void setStatusInfo() {
        try {
            URL url = new URL(mStatus[mCounter].getUrl());
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mImageViewStatus.setImageBitmap(image);
            mTextViewComment.setText(mStatus[mCounter].getComment());
            updateStatus(mStatus[mCounter]);
        } catch (Exception e) {
            Toast.makeText(this, "Hubo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onNext() {
        // LA FUNCION DE CAMBIAR UN ESTADO AL SIGUIENTE
        mCounter = mCounter + 1;
        setStatusInfo();
    }

    @Override
    public void onPrev() {
        if ((mCounter -1) < 0)  {
            return;
        }
        // LA FUNCION DE CAMBIAR UN ESTADO AL ANTERIOR
        mCounter = mCounter - 1;
        setStatusInfo();
    }

    @Override
    public void onComplete() {
        // TERMINARON DE MOSTRARSE TODOS LOS ESTADOS
        finish();
    }

    public void onSwipeRight() {
        // IR HACIA ATRAS
        mStoriesProgressView.reverse();
    }

    public void onSwipeLeft() {
        // IR HACIA ADELANTE
        mStoriesProgressView.skip();
    }

    public void onSwipeTop() {
        // ABRIR UN BOTTOM SHEET
        openBottonSheetViewers();
    }

    public void onSwipeBottom() {

    }

    public void pause() {
        mStoriesProgressView.pause();
    }

    public void start() {
        mStoriesProgressView.resume();
    }



    private void openBottonSheetViewers() {
        if (mStatus[mCounter] != null) {
            mBottomSheetViewers = BottomSheetViewers.newInstance(mStatus[mCounter].getId());
            mBottomSheetViewers.show(getSupportFragmentManager(), mBottomSheetViewers.getTag());
        }
    }


    private class MyGestureListener implements GestureDetector.OnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        public MyGestureListener() {
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}