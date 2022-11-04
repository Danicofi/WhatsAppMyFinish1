package com.dan.whatsappmy.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.models.ChatModel;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ChatsProvider;
import com.dan.whatsappmy.providers.ImageProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmMultiChat extends AppCompatActivity {


    ChatModel mExtraChat;

    TextInputEditText mTextInputGroupName;
    Button mButtonConfirm;
    CircleImageView mCircleImagePhoto;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;
    ChatsProvider mChatProvider;

    Options mOptions;
    ArrayList<String> returnValues = new ArrayList<>();
    File mImageFile;

    String mGroupName = "";
    //FirebaseAuth mAuth;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_multi_user_chat);

        mTextInputGroupName = findViewById(R.id.textInputGroupName);
        mButtonConfirm = findViewById(R.id.btnConfirmGroup);
        mCircleImagePhoto = findViewById(R.id.circleImagePhotoGroup);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        //mAuth = FirebaseAuth.getInstance();
        mImageProvider = new ImageProvider();
        mChatProvider = new ChatsProvider();

        mDialog = new ProgressDialog(ConfirmMultiChat.this);
        mDialog.setTitle("ESPERE UN MOMENTO");
        mDialog.setMessage("Guardando informacion");


        String chat = getIntent().getStringExtra("chat");
        Gson gson = new Gson();
        mExtraChat = gson.fromJson(chat, ChatModel.class);

        for(String id : mExtraChat.getIds()){
            Log.d("USUARIOS","id:" + id);
        }

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
                mGroupName = mTextInputGroupName.getText().toString();
                if (!mGroupName.equals("") && mImageFile != null) {
                    saveImage();
                }
                else {
                    Toast.makeText(ConfirmMultiChat.this, "Debe seleccionar la imagen y ingresar su nombre de usuario", Toast.LENGTH_LONG).show();
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
        Pix.start(ConfirmMultiChat.this, mOptions);
    }

    

    private void goToHomeW() {
        mDialog.show();
        Toast.makeText(ConfirmMultiChat.this, "La informacion se actualizo correctamente", Toast.LENGTH_LONG).show();
        Intent i = new Intent(ConfirmMultiChat.this,HomeW.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void saveImage() {
        mDialog.show();
        mImageProvider.save(ConfirmMultiChat.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            mExtraChat.setGroupChat(mGroupName);
                            mExtraChat.setImageGroup(url);
                           createGroupChat();
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(ConfirmMultiChat.this, "No se pudo almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createGroupChat() {
        mChatProvider.create(mExtraChat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                goToHomeW();
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
                    Pix.start(ConfirmMultiChat.this, mOptions);
                } else {
                    Toast.makeText(ConfirmMultiChat.this, "Por favor concede los permisos para acceder a la camara", Toast.LENGTH_LONG).show();
                }
                //return ;
            }
        }
    }


}