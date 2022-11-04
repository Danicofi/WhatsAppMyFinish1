package com.dan.whatsappmy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.activitys.AddMultiUser;
import com.dan.whatsappmy.activitys.Chat;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MultiUsersAdapter extends FirestoreRecyclerAdapter<User, MultiUsersAdapter.viewHolder> {
    Context context;
    //private FirebaseAuth authP;
    //AuthProvider authP;
    String idAuthHome;
    ArrayList<User> users= new ArrayList<>();
    ArrayList<User> usersFlag= new ArrayList<>();
    ArrayList<String> todosIds = new ArrayList<>();


    public  MultiUsersAdapter(FirestoreRecyclerOptions options, Context context){
        super(options);
        this.context = context;
        //authP = FirebaseAuth.getInstance();
        //authP = new AuthProvider();

    }

    @Override
    protected void onBindViewHolder(@NonNull final MultiUsersAdapter.viewHolder holder, int position, @NonNull final User user) {





       /*if (user.getId().equals(idAuthHome)) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            param.height = 0;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            param.topMargin = 0;
            param.bottomMargin = 0;
            holder.itemView.setVisibility(View.VISIBLE);

            // User is signed in
       }else{
        }*/

        holder.textViewInfoGroup.setText(user.getInfo());
        holder.textViewGroup.setText(user.getUsername());


        if(user.getImage() != null){
            if(!user.getImage().equals("")) {
                Picasso.with(context).load(user.getImage()).into((holder.circleImageUsers));
                usersFlag.add(user);
                todosIds.add(user.getId());
            }
            else {
                holder.circleImageUsers.setImageResource(R.drawable.ic_baseline_person_24);
            }
        }
        else {
            holder.circleImageUsers.setImageResource(R.drawable.ic_baseline_person_24);
        }

        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUser(holder, user);
            }
        });
    }

    private void selectUser(viewHolder holder, User user) {
        if(!user.isSelected()){
            user.setSelected(true);
            holder.imageViewSelectedUsers.setVisibility(View.VISIBLE);
            holder.textViewInfoGroup.getText();
            users.add(user);
        } else{
            user.setSelected(false);
            holder.imageViewSelectedUsers.setVisibility(View.GONE);
            if(users.size()!=0){
            users.remove(user);
            }
        }

        if (!user.getId().equals(idAuthHome)) {
        ((AddMultiUser) context).setUsers(users);
        }

    }


    @NonNull
    @Override
    public MultiUsersAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//instanciar la lista dode queremos trabajar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_multi_users, parent, false);
        return new MultiUsersAdapter.viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewGroup, textViewInfoGroup;
        CircleImageView circleImageUsers;
        ImageView imageViewSelectedUsers;
        View myView;

        public viewHolder(View view){
            super(view);
            myView = view;
            textViewInfoGroup = view.findViewById(R.id.textViewInfoGroup);
            textViewGroup = view.findViewById(R.id.textViewGroup);
            circleImageUsers = view.findViewById(R.id.circlerImageUsers);
            imageViewSelectedUsers = view.findViewById(R.id.imageViewSelected);

        }
    }

    public void setIdAuthHome(String idAuth){
        idAuthHome = idAuth;
    }

}
