package com.dan.whatsappmy.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AppBackgroundHelper {


    public static void online(Context context, boolean status ){
        UsersProvider usersProvider = new UsersProvider();
        AuthProvider auth = new AuthProvider();

        if(auth.getIdAut() != null){
            if(isApplicationSentToBackground(context)) {
                usersProvider.updateOnline(auth.getIdAut(), status);
            }
            else {
                usersProvider.updateOnline(auth.getIdAut(), status);
            }
        }

    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
