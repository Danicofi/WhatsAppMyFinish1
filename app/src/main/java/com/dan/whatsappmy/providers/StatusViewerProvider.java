package com.dan.whatsappmy.providers;

import com.dan.whatsappmy.models.StatusViewer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StatusViewerProvider {

    CollectionReference mCollection;
    AuthProvider mAuthProvider;

    public StatusViewerProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("StatusViewer");
        mAuthProvider = new AuthProvider();
    }

    public void create(final StatusViewer statusViewer) {
        mCollection.document(statusViewer.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    mCollection.document(statusViewer.getId()).set(statusViewer);
                }
            }
        });
    }

    public DocumentReference getStoryViewerById(String id) {
        return mCollection.document(id);
    }

    public Query getStatusViewersByIdStatus(String idStatus) {
        return mCollection.whereEqualTo("idStatus", idStatus).orderBy("timestamp", Query.Direction.DESCENDING);
    }

}
