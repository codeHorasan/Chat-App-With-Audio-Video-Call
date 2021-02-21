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
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.Adapters.FriendRequestsAdapter;
import com.ugur.mychatapp.Models.FriendRequestsModel;
import com.ugur.mychatapp.R;

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private FriendRequestsAdapter adapter;
    private ArrayList<FriendRequestsModel> friendRequestsModels;

    public FriendRequestsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_requests, container,false);

        friendRequestsModels = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view_friend_requests);
        loadFriendRequests();

        return view;
    }

    private void loadFriendRequests() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(User.getInstance().getUuid()).child("FriendRequests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRequestsModels.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("Name").getValue(String.class);
                    Uri imageUri = Uri.parse(ds.child("ImageUri").getValue(String.class));
                    friendRequestsModels.add(new FriendRequestsModel(ds.getKey(), name, imageUri));
                }

                buildAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void buildAdapter() {
        recyclerView.setHasFixedSize(true);
        adapter = new FriendRequestsAdapter(friendRequestsModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
