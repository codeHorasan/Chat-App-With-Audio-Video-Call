package com.ugur.mychatapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ugur.mychatapp.Activities.ChatActivity;
import com.ugur.mychatapp.Models.ChatsFriendModel;
import com.ugur.mychatapp.R;

import java.util.ArrayList;

public class ChatsFriendAdapter extends RecyclerView.Adapter<ChatsFriendAdapter.ChatsFriendsViewHolder> {
    private ArrayList<ChatsFriendModel> mModelList;

    public static class ChatsFriendsViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView imageView;
        public TextView textView;

        public ChatsFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.chats_card_view);
            imageView = itemView.findViewById(R.id.chats_image_view);
            textView = itemView.findViewById(R.id.chats_text_name);
        }
    }

    public ChatsFriendAdapter(ArrayList<ChatsFriendModel> modelList) {
        mModelList = modelList;
    }

    @NonNull
    @Override
    public ChatsFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_chat_friend, parent,false);
        ChatsFriendsViewHolder viewHolder = new ChatsFriendsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsFriendsViewHolder holder, int position) {
        ChatsFriendModel currentItem = mModelList.get(position);
        holder.textView.setText(currentItem.getName());
        Picasso.with(holder.imageView.getContext())
                .load(currentItem.getImageUri())
                .placeholder(R.drawable.image_place_holder)
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("Id", currentItem.getId());
                intent.putExtra("Name", currentItem.getName());
                intent.putExtra("Uri", currentItem.getImageUri().toString());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

}
