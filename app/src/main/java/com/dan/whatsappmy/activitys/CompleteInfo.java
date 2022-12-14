package com.dan.whatsappmy.activitys;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ImageProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.dan.whatsappmy.R;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;

public class CompleteInfo extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    Button mButtonConfirm;
    CircleImageView mCircleImagePhoto;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    Options mOptions;
    ArrayList<String> returnValues = new ArrayList<>();
    File mImageFile;

    String mUsername = "";
    //FirebaseAuth mAuth;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);

        mTextInputUsername = findViewById(R.id.textInputUserName);
        mButtonConfirm = findViewById(R.id.btnConfirm);
        mCircleImagePhoto = findViewById(R.id.circleImagePhoto);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        //mAuth = FirebaseAuth.getInstance();
        mImageProvider = new ImageProvider();

        mDialog = new ProgressDialog(CompleteInfo.this);
        mDialog.setTitle("ESPERE UN MOMENTO");
        mDialog.setMessage("Guardando informacion");

        /*mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(mReturnValues)                               //Pre selected Image Urls
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");*/

        mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(returnValues)                               //Pre selected Image Urls
                .setMode(Options.Mode.All)               //Option to exclude videos
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");                                      //Custom Path For media Storage



        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername = mTextInputUsername.getText().toString();
                if (!mUsername.equals("") && mImageFile != null) {
                    saveImage();
                }
                else {
                    Toast.makeText(CompleteInfo.this, "Debe seleccionar la imagen y ingresar su nombre de usuario", Toast.LENGTH_LONG).show();
                }
            }
        });

        mCircleImagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPix();
            }
        });

    }

    private void startPix() {
        Pix.start(CompleteInfo.this, mOptions);
    }

    private void updateUserInfo(String url) {

        User user = new User();
        user.setUsername(mUsername);
        user.setId(mAuthProvider.getIdAut());
        user.setImage(url);
        mUsersProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                goToHomeW();
                mDialog.show();
                Toast.makeText(CompleteInfo.this, "La informacion se actualizo correctamente", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToHomeW() {
        mDialog.show();
        Toast.makeText(CompleteInfo.this, "La informacion se actualizo correctamente", Toast.LENGTH_LONG).show();
        Intent i = new Intent(CompleteInfo.this,HomeW.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void saveImage() {
        mDialog.show();
        mImageProvider.save(CompleteInfo.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            updateUserInfo(url);
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(CompleteInfo.this, "No se pudo almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mImageFile = new File(returnValues.get(0));
            mCircleImagePhoto.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(CompleteInfo.this, mOptions);
                } else {
                    Toast.makeText(CompleteInfo.this, "Por favor concede los permisos para acceder a la camara", Toast.LENGTH_LONG).show();
                }
                //return ;
            }
        }
    }

}