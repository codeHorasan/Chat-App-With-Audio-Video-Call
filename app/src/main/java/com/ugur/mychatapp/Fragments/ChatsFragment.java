package com.ugur.mychatapp.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ugur.mychatapp.Adapters.ChatsFriendAdapter;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.Models.ChatsFriendModel;
import com.ugur.mychatapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatsFragment extends Fragment {
    private View view;
    RecyclerView recyclerView;
    ChatsFriendAdapter adapter;
    private ArrayList<ChatsFriendModel> modelList;


    public ChatsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats,container,false);
        recyclerView = view.findViewById(R.id.recycler_view_chats);
        modelList = new ArrayList<>();

        loadChats();

        return view;
    }

    public void loadChats() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(User.getInstance().getUuid()).child("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) ds.getValue();
                    Uri imageUri = Uri.parse(map.get("ImageUri"));
                    String targetId = map.get("TargetId");
                    String targetName = map.get("TargetName");
                    modelList.add(new ChatsFriendModel(targetId, targetName, imageUri));
                }

                buildAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void buildAdapter() {
        recyclerView.setHasFixedSize(true);
        adapter = new ChatsFriendAdapter(modelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
