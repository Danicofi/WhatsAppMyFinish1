package com.dan.whatsappmy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.Chat;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends FirestoreRecyclerAdapter<User, ContactsAdapter.viewHolder> {

    Context context;
    //private FirebaseAuth authP;
    AuthProvider authP;


    public  ContactsAdapter(FirestoreRecyclerOptions options, Context context){
        super(options);
        this.context = context;
        //authP = FirebaseAuth.getInstance();
        authP = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull User user) {


        if (user.getId().equals(authP.getIdAut())) {
                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                param.height = 0;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                param.topMargin = 0;
                param.bottomMargin = 0;
                holder.itemView.setVisibility(View.VISIBLE);
            // User is signed in
        }


        holder.textViewInfo.setText(user.getInfo());
        holder.textViewUserName.setText(user.getUsername());

        if(user.getImage() != null){
            if(!user.getImage().equals("")) {
                Picasso.with(context).load(user.getImage()).into((holder.circleImageUser));
            }
            else {
                holder.circleImageUser.setImageResource(R.drawable.ic_baseline_person_24);
            }
        }
        else {
            holder.circleImageUser.setImageResource(R.drawable.ic_baseline_person_24);
        }
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChat(user.getId());
            }
        });
    }

    private void goToChat(String id) {
        Intent i = new Intent(context, Chat.class);
        i.putExtra("idUser", id);
        context.startActivity(i);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//instanciar la lista dode queremos trabajar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacts, parent, false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewUserName, textViewInfo;
        CircleImageView circleImageUser;
        View myView;

        public viewHolder(View view){
            super(view);
            myView = view;
            textViewInfo = view.findViewById(R.id.textViewInfo);
            textViewUserName = view.findViewById(R.id.textViewUserName);
            circleImageUser = view.findViewById(R.id.circlerImageUser);

        }
    }
}
