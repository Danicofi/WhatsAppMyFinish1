package com.dan.whatsappmy.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.fragments.BottomSheetInfo;
import com.dan.whatsappmy.fragments.BottomSheetSelectImage;
import com.dan.whatsappmy.fragments.BottomSheetUserName;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ImageProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.AppBackgroundHelper;
import com.dan.whatsappmy.utils.MyToolBar;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    FloatingActionButton mFabSelectImage;
    BottomSheetSelectImage mBottonSelectedImage;
    UsersProvider mUsersProvider;
    //FirebaseAuth authP;
    AuthProvider authP;
    TextView mUserName;
    TextView mPhone;
    TextView mInfo;
    CircleImageView circleImageProfile;
    User mUser;
    Options mOptions;
    ArrayList<String> returnValues = new ArrayList<>();
    File mImageFile;
    ImageProvider mImageProvider;
    BottomSheetUserName bottomSheetUserName;
    ImageView imageViewEditUserName;
    ListenerRegistration listenerRegistration;
    ImageView imageViewEditInfo;
    BottomSheetInfo bottomSheetInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        MyToolBar.show(this,"Perfil", true);

        mUsersProvider = new UsersProvider();
        //authP = FirebaseAuth.getInstance();
        authP = new AuthProvider();

        mUserName = findViewById(R.id.textViewUserName);
        mPhone = findViewById(R.id.textViewPhone);
        imageViewEditUserName = findViewById(R.id.imageEditUserName);
        imageViewEditInfo = findViewById(R.id.imageEditInfo);
        mInfo = findViewById(R.id.textViewInfo);
        circleImageProfile = findViewById(R.id.circleImageProfile);

        mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(returnValues)                               //Pre selected Image Urls
                .setMode(Options.Mode.All)               //Option to exclude videos
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");


        mFabSelectImage = findViewById(R.id.fabSelectImage);
        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottonSheetSelectImage();
            }
        });

        imageViewEditUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottonSheetUserName();
            }
        });

        imageViewEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottonSheetEditInfo();
            }
        });

        getUserInfo();

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBackgroundHelper.online(Profile.this, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBackgroundHelper.online(Profile.this, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listenerRegistration != null)
        listenerRegistration.remove();
    }

    private void getUserInfo() {
       listenerRegistration = mUsersProvider.getUserInfo(authP.getIdAut()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot!= null){
                    if(documentSnapshot.exists()){
                        mUser = documentSnapshot.toObject(User.class);
                        mUserName.setText(mUser.getUsername());
                        mPhone.setText(mUser.getPhone());
                        mInfo.setText(mUser.getInfo());
                        if(mUser.getImage() != null){
                            if(!mUser.getImage().equals("")){
                                Picasso.with(Profile.this).load(mUser.getImage()).into(circleImageProfile);
                            }
                            else{
                                setImageDefault();
                            }
                        }
                        else{
                            setImageDefault();
                        }
                    }
                }
            }
        });

    }

    private void openBottonSheetEditInfo() {
        if(mUser != null){
            bottomSheetInfo = BottomSheetInfo.newInstance(mUser.getInfo());
            bottomSheetInfo.show(getSupportFragmentManager(),bottomSheetInfo.getTag());
        } else {
            Toast.makeText(this, "La informacion no se pudo cargar", Toast.LENGTH_SHORT).show();
        }
    }

    private void openBottonSheetSelectImage() {
        if(mUser != null){
            mBottonSelectedImage = BottomSheetSelectImage.newInstance(mUser.getImage());
            mBottonSelectedImage.show(getSupportFragmentManager(),mBottonSelectedImage.getTag());
        } else {
            Toast.makeText(this, "La informacion no se pudo cargar", Toast.LENGTH_SHORT).show();
        }
    }

    private void openBottonSheetUserName() {
        if(mUser != null){
            bottomSheetUserName = BottomSheetUserName.newInstance(mUser.getUsername());
            bottomSheetUserName.show(getSupportFragmentManager(),bottomSheetUserName.getTag());
        } else {
            Toast.makeText(this, "La informacion no se pudo cargar", Toast.LENGTH_SHORT).show();
        }
    }

    public void setImageDefault(){
        circleImageProfile.setImageResource(R.drawable.ic_person_white);
    }

   public void startPix() {
        Pix.start(Profile.this, mOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mImageFile = new File(returnValues.get(0));
            circleImageProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(Profile.this, mOptions);
                } else {
                    Toast.makeText(Profile.this, "Por favor concede los permisos para acceder a la camara", Toast.LENGTH_LONG).show();
                }
                //return ;
            }
        }
    }

    private void saveImage() {
        mImageProvider = new ImageProvider();
        mImageProvider.save(Profile.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            mUsersProvider.updateImage(authP.getIdAut(), url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Profile.this, "La imagen se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                else {
                    Toast.makeText(Profile.this, "No se pudo almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}