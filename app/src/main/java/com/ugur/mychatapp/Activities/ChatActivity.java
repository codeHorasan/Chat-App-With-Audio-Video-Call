package com.ugur.mychatapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;
import com.ugur.mychatapp.Adapters.ChatMessageAdapter;
import com.ugur.mychatapp.BaseActivity;
import com.ugur.mychatapp.CallScreenActivity;
import com.ugur.mychatapp.Classes.UploadFile;
import com.ugur.mychatapp.Classes.UploadImage;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.Models.ChatMessageModel;
import com.ugur.mychatapp.R;
import com.ugur.mychatapp.SinchService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends BaseActivity {
    Toolbar toolbar;
    ImageView toolbarImageView;
    TextView toolbarTextView;
    ImageView toolbarAudioCallImage;
    ImageView toolbarVideoCallImage;
    RecyclerView recyclerView;
    ChatMessageAdapter adapter;
    ArrayList<ChatMessageModel> modelList;
    EditText editText;
    ImageView imageView;

    private String chatId;

    private String targetName;
    private String targetId;
    private Uri targetImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chat_toolbar);
        toolbarImageView = findViewById(R.id.chat_toolbar_image);
        toolbarTextView = findViewById(R.id.chat_toolbar_text);
        toolbarAudioCallImage = findViewById(R.id.chat_toolbar_audio_call);
        toolbarVideoCallImage = findViewById(R.id.chat_toolbar_video_call);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.chat_recycler_view);
        editText = findViewById(R.id.chat_edit_text);
        imageView = findViewById(R.id.send_message_image);
        modelList = new ArrayList<>();

        Intent intent = getIntent();
        targetName = intent.getStringExtra("Name");
        targetId = intent.getStringExtra("Id");
        targetImageUri = Uri.parse(intent.getStringExtra("Uri"));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this ,new String[] {Manifest.permission.READ_PHONE_STATE}, 1);
        }

        Picasso.with(getApplicationContext())
                .load(targetImageUri)
                .placeholder(R.drawable.image_place_holder)
                .into(toolbarImageView);

        toolbarTextView.setText(targetName);

        toolbarAudioCallImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                String callId = UUID.randomUUID().toString();
                reference.child("Users").child(targetId).child("Calls").child(User.getInstance().getUuid()).child("CallerName").setValue(User.getInstance().getName());
                reference.child("Users").child(targetId).child("Calls").child(User.getInstance().getUuid()).child("CallerImageUri")
                        .setValue(User.getInstance().getImageUri().toString());
                reference.child("Users").child(targetId).child("Calls").child(User.getInstance().getUuid()).child("CallId").setValue(callId);

                reference.child("Calls").child(callId).child("CallerImageUri").setValue(User.getInstance().getImageUri().toString());
                reference.child("Calls").child(callId).child("TargetImageUri").setValue(targetImageUri.toString());
                reference.child("Calls").child(callId).child("CallerId").setValue(User.getInstance().getUuid());
                reference.child("Calls").child(callId).child("CallerName").setValue(User.getInstance().getName());
                reference.child("Calls").child(callId).child("TargetId").setValue(targetId);
                reference.child("Calls").child(callId).child("TargetName").setValue(targetName);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), AudioCallActivity.class);
                intent.putExtra("pairImageUri", targetImageUri.toString());
                intent.putExtra("pairId", targetId);
                intent.putExtra("pairName", targetName);
                intent.putExtra("callId", callId);
                intent.putExtra("targetId", targetId);
                startActivity(intent);
            }
        });

        toolbarVideoCallImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent goPlaceCallActivity = new Intent(getApplicationContext(), PlaceCallActivity.class);
                System.out.println("HEDEF: " + targetId);
                goPlaceCallActivity.putExtra("targetId", targetId);
                startActivity(goPlaceCallActivity);*/

                Call call = getSinchServiceInterface().callUserVideo(targetId);
                String callId = call.getCallId();
                Intent callScreen = new Intent(v.getContext(), CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            }
        });

        /*recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ( bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter != null && adapter.getItemCount() > 1) {
                                recyclerView.smoothScrollToPosition(adapter.getItemCount());
                                loadMessages();
                            }
                        }
                    }, 100);
                }
            }
        });*/

        getChatId();
    }

    @Override
    protected void onServiceConnected() {
        System.out.println("SERVICE CONNECTED CHAT ACTIVITY");
    }

    public void getChatId() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(User.getInstance().getUuid()).child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) ds.getValue();
                    if (targetId.equals(map.get("TargetId"))) {
                        chatId = ds.getKey();
                        loadMessages();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void loadMessages() {
        if (chatId == null) {
            return;
        }

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Chats").child(chatId).child("Messages");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        HashMap<String, String> map = (HashMap<String, String>) ds.getValue();
                        String senderId = map.get("SenderId");
                        String receiverId = map.get("ReceiverId");
                        String dateString = map.get("DateString");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        try {
                            Date date = sdf.parse(dateString);
                            System.out.println("senderId: " + senderId +" Date: " + dateString);
                            String message = map.get("Message");
                            if (message == null) {
                                if (map.get("fileName") != null) {
                                    //File
                                    try {
                                        Uri fileUri = Uri.parse(map.get("fileUri"));
                                        String fileName = map.get("fileName");
                                        String fileSize = map.get("fileSize");
                                        modelList.add(new ChatMessageModel(senderId, receiverId, date, fileUri, fileName, fileSize));
                                    } catch (Exception e) {
                                        System.out.println("File icon Hata");
                                        loadMessages();
                                    }
                                } else {
                                    //Image
                                    Uri imageUri = null;
                                    try {
                                        imageUri = Uri.parse(map.get("imageUri"));
                                    } catch (Exception e) {
                                        System.out.println("Fotoda hata " + map.get("imageUri"));
                                    }
                                    modelList.add(new ChatMessageModel(senderId, receiverId, date, imageUri));
                                }

                            } else {
                                modelList.add(new ChatMessageModel(message, senderId, receiverId, date));
                            }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Date Parsing Exception");
                            loadMessages();
                        }
                    }

                    Collections.sort(modelList);
                    buildAdapter();
                    recyclerView.scrollToPosition(modelList.size() - 1);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            System.out.println("HATA: " + e.getLocalizedMessage());
            loadMessages();
        }
    }

    public void buildAdapter() {
        //recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        adapter = new ChatMessageAdapter(modelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    public void attachFile(View view) {
        CharSequence options[] = new CharSequence[]{"Image","Document"};
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Select File Type");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,0);
                } else if (which == 1) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*");
                    startActivityForResult(intent,1);
                }
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 0) {
                Uri imageUri = data.getData();
                String imageUUID = UUID.randomUUID().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String dateString = sdf.format(new Date());
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("SharedImages").child(chatId).child(imageUUID + ".");
                if (imageUri != null) {
                    storageReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                UploadImage uploadImage = new UploadImage(User.getInstance().getUuid(), downloadUri.toString());
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference();
                                reference.child("Chats").child(chatId).child("Messages").child(imageUUID).child("imageUri").setValue(uploadImage.getImageUri());
                                reference.child("Chats").child(chatId).child("Messages").child(imageUUID).child("DateString").setValue(dateString);
                                reference.child("Chats").child(chatId).child("Messages").child(imageUUID).child("SenderId").setValue(User.getInstance().getUuid());
                                reference.child("Chats").child(chatId).child("Messages").child(imageUUID).child("ReceiverId").setValue(targetId);
                            }

                            loadMessages();
                        }
                    });
                }
            } else if (requestCode == 1) {
                Uri fileUri = data.getData();
                Cursor returnCursor =
                        getContentResolver().query(fileUri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String fileName = returnCursor.getString(nameIndex);
                String fileSize = Long.toString(returnCursor.getLong(sizeIndex));
                String fileUUID = UUID.randomUUID().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String dateString = sdf.format(new Date());

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("SharedDocuments").child(chatId).child(fileUUID + ".");
                if (fileUri != null) {
                    storageReference.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                UploadFile uploadFile = new UploadFile(User.getInstance().getUuid(), fileName, downloadUri.toString(), fileSize);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference();
                                reference.child("Chats").child(chatId).child("Messages").child(fileUUID).child("fileUri").setValue(uploadFile.getFileUri());
                                reference.child("Chats").child(chatId).child("Messages").child(fileUUID).child("fileName").setValue(uploadFile.getFileName());
                                reference.child("Chats").child(chatId).child("Messages").child(fileUUID).child("fileSize").setValue(uploadFile.getFileSize());
                                reference.child("Chats").child(chatId).child("Messages").child(fileUUID).child("SenderId").setValue(User.getInstance().getUuid());
                                reference.child("Chats").child(chatId).child("Messages").child(fileUUID).child("ReceiverId").setValue(targetId);
                                reference.child("Chats").child(chatId).child("Messages").child(fileUUID).child("DateString").setValue(dateString);
                            }

                            loadMessages();
                        }
                    });

                }
            }
        }
    }

    public void sendMessage(View view) {
        String messageToSend = editText.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference controlReference = database.getReference("Users").child(User.getInstance().getUuid()).child("Chats");

        //Control if Chat exists on User
        controlReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) ds.getValue();
                    if (!map.get("TargetId").equals(null) && map.get("TargetId").equals(targetId)) {
                        exists = true;
                        String chatUUID = ds.getKey();
                        DatabaseReference addMessageReference = database.getReference();
                        String messageUUID = UUID.randomUUID().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String dateString = sdf.format(new Date());
                        addMessageReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("DateString").setValue(dateString);
                        addMessageReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("Message").setValue(messageToSend);
                        addMessageReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("SenderId")
                                .setValue(User.getInstance().getUuid());
                        addMessageReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("ReceiverId").setValue(targetId);

                        addMessageReference.child("Users").child(User.getInstance().getUuid()).child("Chats").child(chatUUID).child("LastDate").setValue(dateString);
                        addMessageReference.child("Users").child(targetId).child("Chats").child(chatUUID).child("LastDate").setValue(dateString);

                        chatId = chatUUID;
                        loadMessages();

                    }
                }

                if (!exists) {
                    String chatUUID = UUID.randomUUID().toString();
                    DatabaseReference createChatReference = database.getReference();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String dateString = sdf.format(new Date());
                    String messageUUID = UUID.randomUUID().toString();
                    createChatReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("DateString").setValue(dateString);
                    createChatReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("Message").setValue(messageToSend);
                    createChatReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("SenderId").setValue(User.getInstance().getUuid());
                    createChatReference.child("Chats").child(chatUUID).child("Messages").child(messageUUID).child("ReceiverId").setValue(targetId);

                    createChatReference.child("Users").child(User.getInstance().getUuid()).child("Chats").child(chatUUID).child("ImageUri").setValue(targetImageUri.toString());
                    createChatReference.child("Users").child(User.getInstance().getUuid()).child("Chats").child(chatUUID).child("TargetId").setValue(targetId);
                    createChatReference.child("Users").child(User.getInstance().getUuid()).child("Chats").child(chatUUID).child("TargetName").setValue(targetName);
                    createChatReference.child("Users").child(User.getInstance().getUuid()).child("Chats").child(chatUUID).child("LastDate").setValue(dateString);

                    createChatReference.child("Users").child(targetId).child("Chats").child(chatUUID).child("TargetId").setValue(User.getInstance().getUuid());
                    createChatReference.child("Users").child(targetId).child("Chats").child(chatUUID).child("TargetName").setValue(User.getInstance().getName());
                    createChatReference.child("Users").child(targetId).child("Chats").child(chatUUID).child("LastDate").setValue(dateString);
                    createChatReference.child("Users").child(targetId).child("Chats").child(chatUUID).child("ImageUri").setValue(User.getInstance().getImageUri().toString());

                    chatId = chatUUID;
                    loadMessages();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerView.scrollToPosition(modelList.size() - 1);
        editText.getText().clear();
    }

    public void goBack(View view) {
        finish();
    }
}