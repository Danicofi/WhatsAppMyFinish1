package com.dan.whatsappmy.providers;

import com.dan.whatsappmy.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {
    private CollectionReference collection;

    public UsersProvider(){
        collection = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    public Query getAllUsersByName(){
        return collection.orderBy("username");
    }

    public DocumentReference getUserInfo(String id) {
        return collection.document(id);
    }

    public Task<Void> create (User user){
        return collection.document(user.getId()).set(user);
    }

    public void createToken(final String idUser) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                collection.document(idUser).update(map);
            }
        });
    }

    public Task<Void> update (User user){
        Map<String, Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("image",user.getImage());
        return collection.document(user.getId()).update(map);
    }

    public Task<Void> updateImage(String id, String url){
        Map<String, Object> map = new HashMap<>();
        map.put("image",url);
        return collection.document(id).update(map);
    }

    public Task<Void> updateUserName(String id, String name){
        Map<String, Object> map = new HashMap<>();
        map.put("username", name);
        return collection.document(id).update(map);
    }

    public Task<Void> updateInfo(String id, String info){
        Map<String, Object> map = new HashMap<>();
        map.put("info", info);
        return collection.document(id).update(map);
    }

    public Task<Void> updateOnline(String idUser, boolean status){
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());
        return collection.document(idUser).update(map);
    }

}