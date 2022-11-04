package com.dan.whatsappmy.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.ShowImageOrVideo;
import com.dan.whatsappmy.models.Message;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import java.io.File;


public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.viewHolder> {

    Context context;
    //private final FirebaseAuth authP;
    private AuthProvider authP;
    UsersProvider usersProvider;
    User user;
    ListenerRegistration listener;

    public MessagesAdapter(FirestoreRecyclerOptions options, Context context){
        super(options);
        this.context = context;
        //authP = FirebaseAuth.getInstance();
        authP = new AuthProvider();
        usersProvider = new UsersProvider();
        user = new User();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull final Message message) {

        holder.textViewMessage.setText(message.getMessage());
        holder.textViewDate.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(),context));

        if(message.getIdSender().equals(authP.getIdAut())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(100,0, 0, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 30 ,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_right));
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.VISIBLE);

            if(message.getStatus().equals("ENVIADO")){
                holder.imageViewCheck.setImageResource(R.drawable.check_gray);
            } else if (message.getStatus().equals("RECIBIDO")){
                holder.imageViewCheck.setImageResource(R.drawable.double_check_gray);
            } else if (message.getStatus().equals("VISTO")){
                holder.imageViewCheck.setImageResource(R.drawable.double_check_blue);
            }

            ViewGroup.MarginLayoutParams marging = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
            marging.rightMargin = 0;

            holder.textViewUserName.setVisibility(View.GONE);


        }
        else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0,0, 100, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(50, 20, 30 ,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_left));
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
            marginDate.rightMargin = 20;

            if(message.getReceivers() != null){
                holder.textViewUserName.setVisibility(View.VISIBLE);
                holder.textViewUserName.setText(message.getUserName());
            }
            else {
                holder.textViewUserName.setVisibility(View.GONE);
            }
        }

        showImage(holder, message);
        showVideo(holder, message);
        showDocument(holder, message);
        openMessage(holder, message);

    }

    private void openMessage(viewHolder holder, final Message message) {
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getType().equals("documento")) {
                    String url = message.getUrl().toString().trim();
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Download:" + message.getMessage());
                    request.setDescription("Download " + message.getMessage());
                    Toast.makeText(context, "Descargando...", Toast.LENGTH_SHORT).show();

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + message.getMessage());

                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);

                    /*File file = new File(context.getExternalFilesDir(null), "file");
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(message.getUrl()))
                            .setTitle(message.getMessage())
                            .setDescription("Download")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationUri(Uri.fromFile(file))
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);*/
                } else if(message.getType().equals("imagen") || message.getType().equals("video")){
                    Intent i = new Intent(context, ShowImageOrVideo.class);
                    String url = message.getUrl().toString();
                    i.putExtra("type", message.getType());
                    i.putExtra("url", url);
                    context.startActivity(i);

                }
            }
        });

    }

    private void showDocument(viewHolder holder, Message message) {

        if (message.getType().equals("documento")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.linearLayoutDocument.setVisibility(View.VISIBLE);
                }
                else {
                    holder.linearLayoutDocument.setVisibility(View.GONE);
                }
            }
            else {
                holder.linearLayoutDocument.setVisibility(View.GONE);
            }
        }
        else {
            holder.linearLayoutDocument.setVisibility(View.GONE);
        }
    }

    private void showVideo(viewHolder holder, Message message) {

        if (message.getType().equals("video")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.frameLayoutVideo.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(message.getUrl()).into(holder.imageViewMessage);

                    if (message.getMessage().equals("\uD83C\uDFA5video")) {
                        holder.textViewMessage.setVisibility(View.GONE);
                        //holder.textViewDate.setPadding(0,0,10,0);
                        ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
                        ViewGroup.MarginLayoutParams marginCheck = (ViewGroup.MarginLayoutParams) holder.imageViewCheck.getLayoutParams();
                        marginDate.topMargin = 15;
                        marginCheck.topMargin = 15;

                    }
                    else {
                        holder.textViewMessage.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    holder.frameLayoutVideo.setVisibility(View.GONE);
                    holder.textViewMessage.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.frameLayoutVideo.setVisibility(View.GONE);
                holder.textViewMessage.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.frameLayoutVideo.setVisibility(View.GONE);
            holder.textViewMessage.setVisibility(View.VISIBLE);
        }

    }

    private void showImage(viewHolder holder, Message message) {

        if (message.getType().equals("imagen")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.imageViewMessage.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(message.getUrl()).into(holder.imageViewMessage);

                    if (message.getMessage().equals("\uD83D\uDCF7imagen")) {
                        holder.textViewMessage.setVisibility(View.GONE);
                        //holder.textViewDate.setPadding(0,0,10,0);
                        ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
                        ViewGroup.MarginLayoutParams marginCheck = (ViewGroup.MarginLayoutParams) holder.imageViewCheck.getLayoutParams();
                        marginDate.topMargin = 15;
                        marginCheck.topMargin = 15;

                    }
                    else {
                        holder.textViewMessage.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    holder.imageViewMessage.setVisibility(View.GONE);
                    holder.textViewMessage.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.imageViewMessage.setVisibility(View.GONE);
                holder.textViewMessage.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.imageViewMessage.setVisibility(View.GONE);
            holder.textViewMessage.setVisibility(View.VISIBLE);
        }

    }



   public ListenerRegistration getListener() {
        return listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//instanciar la lista dode queremos trabajar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new viewHolder(view);
    }


    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewMessage, textViewDate, textViewUserName;
        ImageView imageViewCheck, imageViewMessage, imageViewVideo;
        LinearLayout linearLayoutMessage, linearLayoutDocument;
        View myView, viewVideo;
        FrameLayout frameLayoutVideo;

        public viewHolder(View view){
            super(view);
            myView = view;
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewDate = view.findViewById(R.id.textViewDate);
            imageViewCheck = view.findViewById(R.id.imageViewCheck);
            linearLayoutMessage = view.findViewById(R.id.linearLayoutMessage);
            linearLayoutDocument = view.findViewById(R.id.linearLayoutDocument);
            imageViewMessage = view.findViewById(R.id.imageViewMessage);
            imageViewVideo = view.findViewById(R.id.imageViewVideoChat);
            viewVideo = view.findViewById(R.id.viewVideoChat);
            frameLayoutVideo = view.findViewById(R.id.frameLayoutVideoChat);
            textViewUserName = view.findViewById(R.id.textViewUserNameTheGroup);
        }
    }
}
