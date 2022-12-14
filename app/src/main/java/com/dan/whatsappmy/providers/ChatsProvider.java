package com.dan.whatsappmy.providers;

import com.dan.whatsappmy.activitys.Chat;
import com.dan.whatsappmy.models.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsProvider {

    CollectionReference mCollection;

    public ChatsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public Task<Void> create(ChatModel chat) {
        return mCollection.document(chat.getId()).set(chat);
    }

    public Query getUserChats(String idUser){
        return mCollection.whereArrayContains("ids", idUser).whereGreaterThanOrEqualTo("numberMessages", 1);
    }

    public DocumentReference getChatById(String idChat){
        return mCollection.document(idChat);
    }

    public Query getChatByUser1AndUser2(String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + idUser2);
        ids.add(idUser2 + idUser1);
        return mCollection.whereIn("id", ids);
    }

    public Task<Void> updateWriting(String idChat, String idUser) {
        Map<String, Object> map = new HashMap<>();
        map.put("writing", idUser);
        return mCollection.document(idChat).update(map);
    }

    public void updateNumberMessage(String idChat){
        mCollection.document(idChat).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if(documentSnapshot.contains("numberMessages")){
                        long numberMessage = documentSnapshot.getLong("numberMessages") + 1;
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages", numberMessage);
                        mCollection.document(idChat).update(map);
                    }
                    else{
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages", 1);
                        mCollection.document(idChat).update(map);
                    }
                }
            }
        });

    }

    public void updateNumberMessage1(String idChat){
        mCollection.document(idChat).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if(documentSnapshot.contains("numberMessages")){
                        long numberMessage = documentSnapshot.getLong("numberMessages");
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages", numberMessage);
                        mCollection.document(idChat).update(map);
                    }
                    else{
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages", 1);
                        mCollection.document(idChat).update(map);
                    }
                }
            }
        });

    }


}
