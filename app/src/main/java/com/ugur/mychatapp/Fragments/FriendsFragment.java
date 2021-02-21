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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ugur.mychatapp.Adapters.FriendsAdapter;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.Dialogs.AddFriendDialog;
import com.ugur.mychatapp.Models.FriendsModel;
import com.ugur.mychatapp.R;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    private View view;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private FriendsAdapter adapter;
    private ArrayList<FriendsModel> friendsModels;

    public FriendsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends,container,false);

        friendsModels = new ArrayList<>();
        recyclerView = view.findViewById(R.id.friends_recycler_view);
        floatingActionButton = view.findViewById(R.id.floating_button_add_friend);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendDialog addFriendDialog = new AddFriendDialog();
                addFriendDialog.show(getChildFragmentManager(), "add friend");
            }
        });

        loadFriends();

        return view;
    }

    private void loadFriends() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(User.getInstance().getUuid()).child("Friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsModels.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        Uri imageUri = Uri.parse(ds.child("ImageUri").getValue(String.class));
                        String name = ds.child("Name").getValue(String.class);
                        friendsModels.add(new FriendsModel(ds.getKey(), name, imageUri));
                    } catch (Exception e) {

                    }
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
        adapter = new FriendsAdapter(friendsModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
