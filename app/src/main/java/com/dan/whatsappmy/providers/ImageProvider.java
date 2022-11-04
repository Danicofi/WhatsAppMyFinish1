package com.dan.whatsappmy.providers;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

import com.dan.whatsappmy.models.Message;
import com.dan.whatsappmy.models.Status;
import com.dan.whatsappmy.utils.CompressorBitmapImage;
import com.dan.whatsappmy.utils.ExtensionFile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ImageProvider {

    StorageReference mStorage;
    FirebaseStorage mFirebaseStorage;
    int index;
    MessageProvider mMessagesProvider;
    StatusProvider mStatusProvider;

    public ImageProvider() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorage = mFirebaseStorage.getReference();
        mMessagesProvider = new MessageProvider();
        mStatusProvider = new StatusProvider();
        index = 0;
    }

    public UploadTask save(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = mStorage.child(new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public void uploadMultiple(final Context context, final ArrayList<Message> messages) {

        Uri[] uri = new Uri[messages.size()];
        File file = null;

        if(ExtensionFile.isImageFile(messages.get(index).getUrl())){
            file = CompressorBitmapImage.reduceImageSize(new File(messages.get(index).getUrl()), context);
        } else {
            file = new File(messages.get(index).getUrl());
        }


        uri[index] = Uri.parse("file://" + file.getPath());
        String name = UUID.randomUUID().toString();

        if(ExtensionFile.isImageFile(messages.get(index).getUrl())){
           name = name +".jpg";
        } else {
            name = name + ".mp4";
        }

        final StorageReference ref = mStorage.child(uri[index].getLastPathSegment());
        ref.putFile(uri[index]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            messages.get(index).setUrl(url);
                            mMessagesProvider.create(messages.get(index));
                            index++;
                            if(index< messages.size()){
                                uploadMultiple(context, messages);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void uploadMultipleStatus(final Context context, final ArrayList<Status> statusList) {

        Uri[] uri = new Uri[statusList.size()];
        File file = CompressorBitmapImage.reduceImageSize(new File(statusList.get(index).getUrl()),context);

        uri[index] = Uri.parse("file://" + file.getPath());
        final StorageReference ref = mStorage.child(uri[index].getLastPathSegment());
        ref.putFile(uri[index]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            statusList.get(index).setUrl(url);
                            mStatusProvider.create(statusList.get(index));
                            index++;

                            if (index < statusList.size()) {
                                uploadMultipleStatus(context, statusList);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public Task<Uri> getDownloadUri() {
        return mStorage.getDownloadUrl();
    }

    public Task<Void> delete(String url) {
        return mFirebaseStorage.getReferenceFromUrl(url).delete();
    }


   /* StorageReference mStorage;
    FirebaseStorage mFirebaseStorage;
    int index;
    MessageProvider mMessagesProvider;
    StatusProvider mStatusProvider;

    public ImageProvider() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorage = mFirebaseStorage.getReference();
        mMessagesProvider = new MessageProvider();
        mStatusProvider = new StatusProvider();
        index = 0;
    }

    public UploadTask save(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = mStorage.child(new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public void uploadMultiple(final Context context, final ArrayList<Message> messages) {

        Uri[] uri = new Uri[messages.size()];
        File file = CompressorBitmapImage.reduceImageSize(new File(messages.get(i).getUrl()), context);

        uri[i] = Uri.parse("file://" + file.getPath());
        final StorageReference ref = mStorage.child(uri[i].getLastPathSegment());
        ref.putFile(uri[i]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            messages.get(index).setUrl(url);
                            mMessagesProvider.create(messages.get(index));
                            index++;
                        }
                    });
                }
                else {
                    Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void uploadMultipleStatus(final Context context, final ArrayList<Status> statusList) {

        Uri[] uri = new Uri[statusList.size()];
        File file = CompressorBitmapImage.reduceImageSize(new File(statusList.get(index).getUrl()));

        uri[index] = Uri.parse("file://" + file.getPath());
        final StorageReference ref = mStorage.child(uri[index].getLastPathSegment());
        ref.putFile(uri[index]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            statusList.get(index).setUrl(url);
                            mStatusProvider.create(statusList.get(index));
                            index++;

                            if (index < statusList.size()) {
                                uploadMultipleStatus(context, statusList);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public Task<Uri> getDownloadUri() {
        return mStorage.getDownloadUrl();
    }

    public Task<Void> delete(String url) {
        return mFirebaseStorage.getReferenceFromUrl(url).delete();
    }*/
}
