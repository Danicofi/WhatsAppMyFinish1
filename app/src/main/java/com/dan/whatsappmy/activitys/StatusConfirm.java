package com.dan.whatsappmy.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.adapters.StatusPagerAdapter;
import com.dan.whatsappmy.models.Status;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ImageProvider;
import com.dan.whatsappmy.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.Date;

public class StatusConfirm extends AppCompatActivity {

    ViewPager mViewPager;
    ArrayList<String> data;

    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    ArrayList<Status> mStatus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_confirm);
        setStatusBarColor();

        mViewPager = findViewById(R.id.viewPager);
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();

        data = getIntent().getStringArrayListExtra("data");

        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                long now = new Date().getTime();
                long limit = now + (60 *1000 * 10);

                Status s = new Status();
                s.setIdUser(mAuthProvider.getIdAut());
                s.setComment("");
                s.setTimestamp(now);
                s.setTimestampLimit(limit);
                s.setUrl(data.get(i));
                mStatus.add(s);
            }
        }

        StatusPagerAdapter pagerAdapter = new StatusPagerAdapter(
                getApplicationContext(),
                getSupportFragmentManager(),
                dpToPixels(2, this),
                data
        );
        ShadowTransformer transformer = new ShadowTransformer(mViewPager, pagerAdapter);
        transformer.enableScaling(true);

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(false, transformer);
    }

    public void send() {
        mImageProvider.uploadMultipleStatus(StatusConfirm.this, mStatus);
        finish();
    }

    public void setComment(int position, String comment) {
        mStatus.get(position).setComment(comment);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
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