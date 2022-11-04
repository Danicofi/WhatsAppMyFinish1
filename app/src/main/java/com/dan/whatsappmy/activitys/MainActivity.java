package com.dan.whatsappmy.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.dan.whatsappmy.R;
import com.dan.whatsappmy.providers.AuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button buttonSendGo;
    EditText editTextPhone;
    CountryCodePicker countryCode;

    FirebaseFirestore miFirestore;
    //FirebaseAuth mAuth = null;
    AuthProvider mAuthProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSendGo = findViewById(R.id.btnSend);
        editTextPhone = findViewById(R.id.texTelefono);
        countryCode = findViewById(R.id.ccp);

        //miFirestore = FirebaseFirestore.getInstance();
        mAuthProvider = new AuthProvider();
        //mAuth = new AuthProvider();

        buttonSendGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                //goToVerificationCodeActivity();
                //saveData();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
       // requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


        //verificar la conexion con Firebase
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getSessionUser() != null){
            Intent i = new Intent(MainActivity.this,HomeW.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }


    private void saveData(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Frida");
        miFirestore.collection("Users").document().set(map);
    }

    private void getData (){
        String code = countryCode.getSelectedCountryCodeWithPlus();
        String phone = editTextPhone.getText().toString();
        if(phone.equals("")){
            Toast.makeText(this, "Debe ingresar el telefono", Toast.LENGTH_LONG).show();
            editTextPhone.setText("Ingrese el código del país y el número de teléfono");
            editTextPhone.setTextColor(Color.RED);
            editTextPhone.setVisibility(View.VISIBLE);
        }
        else{
            goToVerificationCodeActivity(code + phone);

            //Toast.makeText(this, "Telefono " + code + phone, Toast.LENGTH_LONG).show();
        }
    }

    private void goToVerificationCodeActivity (String phone){

        Intent i = new Intent(MainActivity.this, VerificationCode.class);
        i.putExtra("phone", phone);
        startActivity(i);

    }


}