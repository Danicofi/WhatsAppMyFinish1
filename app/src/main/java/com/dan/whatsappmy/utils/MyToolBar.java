package com.dan.whatsappmy.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dan.whatsappmy.R;

public class MyToolBar {

    public static void show(AppCompatActivity activity, String title, Boolean upButton){
        Toolbar toolbar = activity.findViewById(R.id.toolBar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
