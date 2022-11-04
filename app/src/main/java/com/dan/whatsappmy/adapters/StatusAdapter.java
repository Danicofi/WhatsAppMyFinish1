package com.dan.whatsappmy.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.StatusDetail;
import com.dan.whatsappmy.models.Status;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.MessageProvider;
import com.dan.whatsappmy.providers.StatusViewerProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.RelativeTime;
import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    FragmentActivity context;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    MessageProvider messagesProvider;
    StatusViewerProvider statusViewerProvider;
    User user;

    ArrayList<Status> statusList;

    Gson gson = new Gson();

    public StatusAdapter(FragmentActivity context, ArrayList<Status> statusList) {
        this.context = context;
        this.statusList = statusList;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        messagesProvider = new MessageProvider();
        statusViewerProvider = new StatusViewerProvider();
        user = new User();
    }


    private void getUserInfo(final ViewHolder holder, String idUser) {

        usersProvider.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        holder.textViewUsername.setText(user.getUsername());
                    }
                }

            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Status[] statusGSON = gson.fromJson(statusList.get(position).getJson(), Status[].class);
        holder.circularStatusView.setPortionsCount(statusGSON.length);

        setPortionsColor(statusGSON, holder, position);
        setImageStatus(statusGSON, holder);
        getUserInfo(holder, statusList.get(position).getIdUser());

        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StatusDetail.class);
                intent.putExtra("status", statusList.get(position).getJson());

                // significa que el usuario ya observo todos los estados
                if ((statusList.get(position).getCounter() + 1) == statusGSON.length) {
                    intent.putExtra("counter", 0);
                }
                else {
                    intent.putExtra("counter", statusList.get(position).getCounter() + 1);
                }

                context.startActivity(intent);
            }
        });
    }

    private void setPortionsColor(Status[] statusGSON, final ViewHolder holder, final int position) {

        for (int i = 0; i < statusGSON.length; i++) {
            final int finalI = i;
            statusViewerProvider.getStoryViewerById(authProvider.getIdAut() + statusGSON[i].getId()).addSnapshotListener(context, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (documentSnapshot != null) {
                        // saber si el usuario ya miro un estado
                        if (documentSnapshot.exists()) {
                            holder.circularStatusView.setPortionColorForIndex(finalI, context.getResources().getColor(R.color.colorGrisStatus));
                            statusList.get(position).setCounter(finalI);
                        }
                        else {
                            holder.circularStatusView.setPortionColorForIndex(finalI, context.getResources().getColor(R.color.colorGreenStatus));
                        }

                    }
                    else {
                        holder.circularStatusView.setPortionColorForIndex(finalI, context.getResources().getColor(R.color.colorGreenStatus));
                        statusList.get(position).setCounter(0);
                    }
                }
            });

        }

    }

    private void setImageStatus(Status[] statusGSON, ViewHolder holder) {
        if (statusGSON.length > 0) {
            Picasso.with(context).load(statusGSON[statusGSON.length - 1].getUrl()).into(holder.circleImageUser);
            String relativeTime = RelativeTime.timeFormatAMPM(statusGSON[statusGSON.length - 1].getTimestamp(), context);
            holder.textViewDate.setText(relativeTime);
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUsername;
        TextView textViewDate;
        CircleImageView circleImageUser;
        CircularStatusView circularStatusView;

        View myView;

        public ViewHolder(View view) {
            super(view);
            myView = view;
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewDate = view.findViewById(R.id.textViewDate);
            circleImageUser = view.findViewById(R.id.circlerImageUserStatus);
            circularStatusView = view.findViewById(R.id.circularStatusView);
        }

    }
}

