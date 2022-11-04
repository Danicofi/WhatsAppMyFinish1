package com.dan.whatsappmy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.models.StatusViewer;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.MessageProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusViewersAdapter extends FirestoreRecyclerAdapter<StatusViewer, StatusViewersAdapter.viewHolder> {

    Context context;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    MessageProvider messagesProvider;
    User user;
    ListenerRegistration listener;
    ListenerRegistration listenerLastMessage;

    public StatusViewersAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        messagesProvider = new MessageProvider();
        user = new User();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull final StatusViewer statusViewer) {
        String relativeTime = RelativeTime.timeFormatAMPM(statusViewer.getTimestamp(), context);
        holder.textViewDate.setText(relativeTime);
        getUserInfo(holder, statusViewer.getIdUser());
    }


    private void getUserInfo(final viewHolder holder, String idUser) {
        usersProvider.getUserInfo(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        holder.textViewUsername.setText(user.getUsername());
                        if (user.getImage() != null) {
                            if (!user.getImage().equals("")) {
                                Picasso.with(context).load(user.getImage()).into(holder.circleImageUser);
                            }
                            else {
                                holder.circleImageUser.setImageResource(R.drawable.ic_baseline_person_24);
                            }
                        }
                        else {
                            holder.circleImageUser.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    }
                }
            }
        });
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_status_viewers, parent, false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView textViewUsername;
        TextView textViewDate;
        CircleImageView circleImageUser;
        View myView;

        public viewHolder(View view) {
            super(view);
            myView = view;
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewDate = view.findViewById(R.id.textViewDate);
            circleImageUser = view.findViewById(R.id.circlerImageUserStatusViewer);

        }

    }
}
