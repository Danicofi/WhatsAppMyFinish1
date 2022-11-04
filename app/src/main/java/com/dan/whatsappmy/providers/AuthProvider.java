package com.dan.whatsappmy.providers;




import android.app.Activity;
import android.content.Context;

import com.dan.whatsappmy.activitys.StatusConfirm;
import com.dan.whatsappmy.activitys.VerificationCode;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthProvider {

    private FirebaseAuth mAuth;

    public AuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void sendCodeVerification(Context cont,String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks callback) {

       PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity((VerificationCode) cont)                 // Activity (for callback binding)
                        .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


    public void setmAuth(FirebaseAuth authVer){
        mAuth = authVer;
    }

    public Task<AuthResult> signInPhone(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        return mAuth.signInWithCredential(credential);
    }

    public FirebaseUser getSessionUser() {
        return mAuth.getCurrentUser();
    }

    public String getIdAut() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        else {
            return null;
        }
    }

    public void signOut() {
        mAuth.signOut();
    }
}
