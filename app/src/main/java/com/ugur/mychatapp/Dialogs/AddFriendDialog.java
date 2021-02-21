package com.ugur.mychatapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.R;

import java.util.HashMap;

public class AddFriendDialog extends AppCompatDialogFragment {
    private EditText editText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_friend, null);

        editText = view.findViewById(R.id.add_friend_edit_text);

        System.out.println("User URI: " + User.getInstance().getImageUri());

        builder.setView(view)
                .setTitle("Send Friend Request")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String friendRequestInfo = editText.getText().toString().trim();
                        sendFriendRequest(friendRequestInfo);
                    }
                });

        return builder.create();
    }

    private void sendFriendRequest(String friendRequestInfo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String,String> map = (HashMap<String, String>) ds.getValue();
                    String controlString = map.get("Name") + map.get("Id");
                    if (controlString.matches(friendRequestInfo) && !controlString.matches(User.getInstance().getName() + User.getInstance().getId())) {
                        String targetUUID = ds.getKey();
                        reference.child(targetUUID).child("FriendRequests").child(User.getInstance().getUuid()).child("Name").setValue(User.getInstance().getName());
                        reference.child(targetUUID).child("FriendRequests").child(User.getInstance().getUuid()).child("ImageUri").setValue(User.getInstance().getImageUri().toString());
                        //Date
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
