package com.ugur.mychatapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.Models.FriendRequestsModel;
import com.ugur.mychatapp.R;

import java.util.ArrayList;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestsViewHolder> {
    ArrayList<FriendRequestsModel> mModelList;

    public static class FriendRequestsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ImageView acceptImage;
        public ImageView rejectImage;

        public FriendRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.request_image_view);
            textView = itemView.findViewById(R.id.request_text_name);
            acceptImage = itemView.findViewById(R.id.request_accept_image);
            rejectImage = itemView.findViewById(R.id.request_cancel_image);
        }
    }

    public FriendRequestsAdapter(ArrayList<FriendRequestsModel> modelList) {
        mModelList = modelList;
    }

    @NonNull
    @Override
    public FriendRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_friend_request,parent,false);
        FriendRequestsViewHolder viewHolder = new FriendRequestsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestsViewHolder holder, int position) {
        FriendRequestsModel currentItem = mModelList.get(position);
        holder.textView.setText(currentItem.getName());
        Picasso.with(holder.imageView.getContext())
                .load(currentItem.getImageUri())
                .placeholder(R.drawable.image_place_holder)
                .into(holder.imageView);

        holder.acceptImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModelList.remove(position);
                notifyDataSetChanged();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();

                reference.child("Users").child(currentItem.getId()).child("Friends")
                        .child(User.getInstance().getUuid()).child("Name").setValue(User.getInstance().getName());
                reference.child("Users").child(currentItem.getId()).child("Friends")
                        .child(User.getInstance().getUuid()).child("ImageUri").setValue(User.getInstance().getImageUri().toString());

                reference.child("Users").child(User.getInstance().getUuid()).child("Friends")
                        .child(currentItem.getId()).child("Name").setValue(currentItem.getName());
                reference.child("Users").child(User.getInstance().getUuid()).child("Friends")
                        .child(currentItem.getId()).child("ImageUri").setValue(currentItem.getImageUri().toString());

                reference.child("Users").child(User.getInstance().getUuid()).child("FriendRequests").child(currentItem.getId()).removeValue();

            }
        });

        holder.rejectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModelList.remove(position);
                notifyDataSetChanged();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                reference.child("Users").child(User.getInstance().getUuid()).child("FriendRequests").child(currentItem.getId()).removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }


}
