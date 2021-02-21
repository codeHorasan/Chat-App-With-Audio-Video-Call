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
import com.ugur.mychatapp.Models.FriendsModel;
import com.ugur.mychatapp.R;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    private ArrayList<FriendsModel> mModelList;

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView imageView;
        public TextView textView;
        public ImageView removeImage;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.friends_card_view);
            imageView = itemView.findViewById(R.id.friend_image_view);
            textView = itemView.findViewById(R.id.friend_text_name);
            removeImage = itemView.findViewById(R.id.remove_friend_image);
        }
    }

    public FriendsAdapter(ArrayList<FriendsModel> modelList) {
        mModelList = modelList;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_friend,parent,false);
        FriendsViewHolder viewHolder = new FriendsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        FriendsModel currentItem = mModelList.get(position);
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

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Sil");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

}
