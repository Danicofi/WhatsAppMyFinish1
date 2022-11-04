package com.dan.whatsappmy.activitys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.whatsappmy.R;
import com.dan.whatsappmy.adapters.MessagesAdapter;
import com.dan.whatsappmy.models.ChatModel;
import com.dan.whatsappmy.models.Message;
import com.dan.whatsappmy.models.User;
import com.dan.whatsappmy.providers.AuthProvider;
import com.dan.whatsappmy.providers.ChatsProvider;
import com.dan.whatsappmy.providers.FilesProvider;
import com.dan.whatsappmy.providers.MessageProvider;
import com.dan.whatsappmy.providers.NotificationProvider;
import com.dan.whatsappmy.providers.UsersProvider;
import com.dan.whatsappmy.utils.AppBackgroundHelper;
import com.dan.whatsappmy.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMulti extends AppCompatActivity {

    String mExtraIdUser;
    String mExtraidChat;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessageProvider mMessagesProvider;
    FilesProvider mFilesProvider;
    NotificationProvider mNotificationProvider;

    ImageView mImageViewBack;
    TextView mTextViewUsername;
    TextView mTextViewOnline;
    CircleImageView mCircleImageUser;
    EditText mEditTextMessage;
    ImageView mImageViewSend;
    ImageView mImageViewSelectFile;

    ImageView mImageViewSelectPictures;

    MessagesAdapter mAdapter;
    RecyclerView mRecyclerViewMessages;
    LinearLayoutManager mLinearLayoutManager;

    Timer mTimer;

    ListenerRegistration mListenerChat;

    User mMyUser;

    Options mOptions;
    ArrayList<String> mReturnValues = new ArrayList<>();

    final int ACTION_FILE = 2;
    ArrayList<Uri> mFileList;

    ChatModel mChat;


    ArrayList<String> mReceiversId = new ArrayList<>();
    ArrayList<User> mReceivers = new ArrayList<>();

    int mCount = 0;
    String mReceiversName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_multi);

        String chat = getIntent().getStringExtra("chat");
        Gson gson = new Gson();
        mChat = gson.fromJson(chat, ChatModel.class);

        mExtraIdUser = getIntent().getStringExtra("idUser");
        mExtraidChat = mChat.getId();

        Log.d("EXTRA", "ID CHAT: " + mExtraidChat);
        Log.d("EXTRA", "ID USER: " + mExtraIdUser);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessageProvider();
        mFilesProvider = new FilesProvider();
        mNotificationProvider = new NotificationProvider();

        for(String id: mChat.getIds()){
            if(!id.equals(mAuthProvider.getIdAut())){
                //Añadir todos los ususarios que participan en el chat
                mReceiversId.add(id);
            }
        }

        mEditTextMessage = findViewById(R.id.editTextMessageGroup);
        mImageViewSend = findViewById(R.id.imageViewSendGroup);
        mImageViewSelectPictures = findViewById(R.id.imageViewSelectPicturesGroup);
        mImageViewSelectFile = findViewById(R.id.imageViewSelectFilesGroup);
        mRecyclerViewMessages = findViewById(R.id.rvMessageGroup);

        mLinearLayoutManager = new LinearLayoutManager(ChatMulti.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessages.setLayoutManager(mLinearLayoutManager);

        mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(mReturnValues)                               //Pre selected Image Urls
                .setMode(Options.Mode.All)//Option to exclude videos
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");


        showChatToolbar(R.layout.chat_toolbar);
        getMyInfo();
        getReceiversInfo();

        checkIfExistChat();
        setWriting();

        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createMessage();
            }
        });

        mImageViewSelectPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPix();
            }
        });

        mImageViewSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
            }
        });
    }

    private void getReceiversInfo() {
        for(String id: mReceiversId){
            mUsersProvider.getUserInfo(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    mReceivers.add(user);
                    mCount++;

                    if(mCount == mReceiversId.size()){
                        for(User u:mReceivers){
                            mReceiversName = mReceiversName + u.getUsername() + ", ";
                        }
                        mTextViewOnline.setText(mReceiversName);
                    }
                }
            });
        }
    }

    private void selectFiles() {
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), ACTION_FILE);
    }



    //Metodo para que se vea el escribiendo
    private void setWriting() {
        mEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // ESTA ESCRIBIENDO
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mTimer != null) {
                    if (mExtraidChat != null) {
                        mChatsProvider.updateWriting(mExtraidChat, mAuthProvider.getIdAut());
                        mTimer.cancel();
                    }
                }
            }

            // SI EL USUARIO DEJO DE ESCRIBIR
            @Override
            public void afterTextChanged(Editable editable) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mExtraidChat != null) {
                            mChatsProvider.updateWriting(mExtraidChat, "");
                        }
                    }
                }, 2000);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBackgroundHelper.online(ChatMulti.this, true);
        if (mAdapter != null) {
            mRecyclerViewMessages.getRecycledViewPool().clear();
            mAdapter.notifyDataSetChanged();
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListenerChat != null) {
            mListenerChat.remove();
        }
    }

    private void startPix() {
        Pix.start(ChatMulti.this, mOptions);
    }

    private void createMessage() {

        String textMessage = mEditTextMessage.getText().toString();
        if (!textMessage.equals("")) {
            final Message message = new Message();
            message.setIdChat(mChat.getId());
            message.setIdSender(mAuthProvider.getIdAut());
            message.setIdReceiver(mExtraIdUser);
            message.setMessage(textMessage);
            message.setStatus("ENVIADO");
            message.setType("texto");
            message.setTimestamp(new Date().getTime());
            message.setReceivers(mReceiversId);
            message.setUserName(mMyUser.getUsername());

            mMessagesProvider.create(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mEditTextMessage.setText("");
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    mChatsProvider.updateNumberMessage(mExtraidChat);
                    getLastMessages(message);
                    //sendNotification(message.getMessage());
                    //Toast.makeText(ChatActivity.this, "El mensaje se creo correctamente", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {
            Toast.makeText(this, "Ingresa el mensaje", Toast.LENGTH_SHORT).show();
        }

    }

    private void getLastMessages(final Message message) {
        mMessagesProvider.getLastMessagesByChatAndSender(mExtraidChat, mAuthProvider.getIdAut()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (querySnapshot != null) {
                    ArrayList<Message> messages = new ArrayList<>();

                    for(DocumentSnapshot document: querySnapshot.getDocuments()) {
                        Message m = document.toObject(Message.class);
                        messages.add(m);
                    }

                    if (messages.size() == 0) {
                        messages.add(message);
                    }
                    Collections.reverse(messages);
                    sendNotification(messages);
                }
            }
        });
    }

    private void sendNotification(ArrayList<Message> messages) {
        Map<String, String> data = new HashMap<>();
        data.put("title", "MENSAJE");
        data.put("body", "texto mensaje");
        data.put("idNotification", String.valueOf(mChat.getIdNotification()));
        data.put("usernameReceiver","");
        data.put("usernameSender", mMyUser.getUsername());
        data.put("imageReceiver", "");
        data.put("imageSender", mMyUser.getImage());
        data.put("idChat", mExtraidChat);
        data.put("idSender", mAuthProvider.getIdAut());
        data.put("idReceiver","");
        data.put("tokenSender", mMyUser.getToken());
        data.put("tokenReceiver", "");

        Gson gson = new Gson();
        String messagesJSON = gson.toJson(messages);
        data.put("messagesJSON", messagesJSON);

        List<String> tokens = new ArrayList<>();

        for(User us: mReceivers){
            tokens.add(us.getToken());
        }


        mNotificationProvider.send(ChatMulti.this, tokens, data);
    }

    private void checkIfExistChat() {
                    if (mExtraidChat == null) {
                        createChat();
                    } else {
                        getMessagesByChat();
                        updateStatus();
                        //Toast.makeText(ChatActivity.this, "El chat entre dos usuarios ya existe", Toast.LENGTH_SHORT).show();
                    }
    }



    private void updateStatus() {
        mMessagesProvider.getMessagesNotRead(mExtraidChat).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot document: queryDocumentSnapshots.getDocuments()) {
                    Message message = document.toObject(Message.class);
                    if (!message.getIdSender().equals(mAuthProvider.getIdAut())) {
                        mMessagesProvider.updateStatus(message.getId(), "VISTO");
                    }
                }
            }
        });
    }

    private void getMessagesByChat() {

        mChatsProvider.updateNumberMessage1(mChat.getId());
        Query query = mMessagesProvider.getMessagesByChat(mChat.getId());

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        mAdapter = new MessagesAdapter(options, ChatMulti.this);
        mRecyclerViewMessages.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateStatus();
                int numberMessage = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberMessage - 1) && lastMessagePosition == (positionStart -1))) {
                    mRecyclerViewMessages.scrollToPosition(positionStart);
                }
            }
        });
        //mAdapter.notifyDataSetChanged();
    }

    private void createChat() {

        Random random = new Random();
        int n = random.nextInt(100000);
        mChat = new ChatModel();
        mChat.setId(mAuthProvider.getIdAut() + mExtraIdUser);
        mChat.setTimestamp(new Date().getTime());
        mChat.setNumberMessages(0);
        mChat.setWriting("");
        mChat.setIdNotification(n);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getIdAut());
        ids.add(mExtraIdUser);

        mChat.setIds(ids);

        mExtraidChat = mChat.getId();

        mChatsProvider.create(mChat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getMessagesByChat();
                //Toast.makeText(ChatActivity.this, "El chat se creo correctamente", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getMyInfo() {
        mUsersProvider.getUserInfo(mAuthProvider.getIdAut()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    mMyUser = documentSnapshot.toObject(User.class);
                }
            }
        });
    }



    private void showChatToolbar (int resource) {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resource, null);
        actionBar.setCustomView(view);

        mTextViewOnline = findViewById(R.id.textViewOnline);
        mImageViewBack = view.findViewById(R.id.imageViewBack);
        mTextViewUsername = view.findViewById(R.id.textViewUserNameToolBar);
        mCircleImageUser = view.findViewById(R.id.circleImageUserToolbar);

        Picasso.with(ChatMulti.this).load(mChat.getImageGroup()).into(mCircleImageUser);
        mTextViewUsername.setText(mChat.getGroupChat());

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Intent intent = new Intent(ChatMulti.this, ConfirmImageSend.class);
            intent.putExtra("data", mReturnValues);
            intent.putExtra("idChat", mExtraidChat);
            intent.putExtra("idReceiver", mExtraIdUser);

            Gson gson = new Gson();
            String myUserJSON = gson.toJson(mMyUser);

            intent.putExtra("myUser", myUserJSON);
            intent.putExtra("idNotification", String.valueOf(mChat.getIdNotification()));
            startActivity(intent);
        }

        if (requestCode == ACTION_FILE && resultCode == RESULT_OK) {
            mFileList = new ArrayList<>();

            ClipData clipData = data.getClipData();

            // SELECCIONO UN SOLO ARCHIVO
            if (clipData == null) {
                Uri uri = data.getData();
                mFileList.add(uri);
            }
            // SELECCIONO VARIOS ARCHIVOS
            else {
                int count = clipData.getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    mFileList.add(uri);
                }
            }

            mFilesProvider.saveFiles(ChatMulti.this, mFileList, mExtraidChat, mExtraIdUser);

            final Message message = new Message();
            message.setIdChat(mExtraidChat);
            message.setIdSender(mAuthProvider.getIdAut());
            message.setIdReceiver(mExtraIdUser);
            message.setMessage("\uD83D\uDCC4 Documento");
            message.setStatus("ENVIADO");
            message.setType("texto");
            message.setTimestamp(new Date().getTime());

            ArrayList<Message> messages = new ArrayList<>();
            messages.add(message);

            sendNotification(messages);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(ChatMulti.this, mOptions);
                } else {
                    Toast.makeText(ChatMulti.this, "Por favor concede los permisos para acceder a la camara", Toast.LENGTH_LONG).show();
                }
                //return;
            }
        }
    }
}