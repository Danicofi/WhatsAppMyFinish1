package com.dan.whatsappmy.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.dan.whatsappmy.models.User;

import java.util.concurrent.TimeUnit;


public class VerificationCode extends AppCompatActivity {

    Button buttonVerification;
    String extraPhone, mVerificationId;
    //PhoneAuthProvider.ForceResendingToken mResendToken;
    AuthProvider miAuthProvider;
    //AuthProvider miAuthProvider;
    EditText editTextCode;
    TextView miTextViewSMS;
    ProgressBar miProgressBar;
    UsersProvider mUserProvider;
    //public FirebaseAuth mAuth1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        buttonVerification = findViewById(R.id.btnVerification);
        miAuthProvider = new AuthProvider();
        extraPhone = getIntent().getStringExtra("phone");
        editTextCode = findViewById(R.id.editTextCodeVerification);
        miTextViewSMS = findViewById(R.id.textViewSMS);
        miProgressBar = findViewById(R.id.progressBar);
        //mAuth1 = FirebaseAuth.getInstance();
        mUserProvider = new UsersProvider();

        miAuthProvider.sendCodeVerification(VerificationCode.this, extraPhone, mCallbacks);

       /*PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth1)
                        .setPhoneNumber(extraPhone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        miAuthProvider.setmAuth(mAuth1);*/

        buttonVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigo = editTextCode.getText().toString();
                if(!codigo.equals("") && codigo.length() >= 6){
                    signIn(codigo);
                }
                else {
                    Toast.makeText(VerificationCode.this, "Ingresa el codigo", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            miProgressBar.setVisibility(View.GONE);
            miTextViewSMS.setVisibility(View.GONE);
            String verCode = phoneAuthCredential.getSmsCode();
            if(verCode != null){
                editTextCode.setText(verCode);
                signIn(verCode);
            }
            else {
                Toast.makeText(VerificationCode.this, "NO SE ENVIO NADA", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            miProgressBar.setVisibility(View.GONE);
            miTextViewSMS.setVisibility(View.GONE);
            Toast.makeText(VerificationCode.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            miProgressBar.setVisibility(View.GONE);
            miTextViewSMS.setVisibility(View.GONE);
            Toast.makeText(VerificationCode.this, "EL CODIGO SE ENVIO", Toast.LENGTH_LONG).show();
            mVerificationId = verificationId;
        }
    };

    private void signIn(String code) {
       // PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        miAuthProvider.signInPhone(mVerificationId, code).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final User user = new User();

                        user.setId(miAuthProvider.getIdAut());
                        user.setPhone(extraPhone);

                        mUserProvider.getUserInfo(miAuthProvider.getIdAut()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (!documentSnapshot.exists()) {
                                    mUserProvider.create(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            goToCompleteInfo();
                                        }
                                    });
                                } else if(documentSnapshot.contains("username") && documentSnapshot.contains("image")) {
                                    String userName = documentSnapshot.getString("username");
                                    String image = documentSnapshot.getString("image");
                                    if(userName != null && image != null){
                                        if(!userName.equals("") && !image.equals("")){
                                            goToHomeW();
                                        }
                                        else {
                                            goToCompleteInfo();
                                        }
                                    }else{
                                        goToCompleteInfo();
                                    }
                                }
                                else{
                                    goToCompleteInfo();
                                }
                            }
                        });

                }
                else {
                    Toast.makeText(VerificationCode.this, "No se pudo autenticar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToHomeW() {
        Intent i = new Intent(VerificationCode.this,HomeW.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }



    private void goToCompleteInfo(){
        Intent i = new Intent(VerificationCode.this, CompleteInfo.class);
        //intentVerificationCode.putExtra("phone", extraPhone);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}