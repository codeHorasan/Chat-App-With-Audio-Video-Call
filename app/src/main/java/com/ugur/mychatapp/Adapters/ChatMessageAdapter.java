package com.ugur.mychatapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.Models.ChatMessageModel;
import com.ugur.mychatapp.R;

import java.util.ArrayList;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {
    private ArrayList<ChatMessageModel> mModelList;

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout constraintLayout;
        public CardView cardViewRight;
        public TextView textViewRight;
        public ImageView imageViewFileRight;
        public CardView cardViewLeft;
        public TextView textViewLeft;
        public ImageView imageViewFileLeft;
        public ImageView imageRight;
        public ImageView imageLeft;

        public ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraint_layout_message);
            cardViewRight = itemView.findViewById(R.id.card_view_message_right);
            textViewRight = itemView.findViewById(R.id.text_view_message_right);
            imageViewFileRight = itemView.findViewById(R.id.file_image_view_message_right);
            cardViewLeft = itemView.findViewById(R.id.card_view_message_left);
            textViewLeft = itemView.findViewById(R.id.text_view_message_left);
            imageViewFileLeft = itemView.findViewById(R.id.file_image_view_message_left);
            imageRight = itemView.findViewById(R.id.image_message_right);
            imageLeft = itemView.findViewById(R.id.image_message_left);
        }
    }

    public ChatMessageAdapter(ArrayList<ChatMessageModel> modelList) {
        mModelList = modelList;
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_message, parent,false);
        ChatMessageViewHolder viewHolder = new ChatMessageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessageModel currentItem = mModelList.get(position);

        if (currentItem.getTYPE().equals("TEXT")) {
            if (User.getInstance().getUuid().equals(currentItem.getSenderId())) {
                holder.textViewRight.setText(currentItem.getMessage());
                holder.cardViewRight.setVisibility(View.VISIBLE);
            } else {
                holder.textViewLeft.setText(currentItem.getMessage());
                holder.cardViewLeft.setVisibility(View.VISIBLE);
            }
        } else if (currentItem.getTYPE().equals("FILE")) {
            if (User.getInstance().getUuid().equals(currentItem.getSenderId())) {
                holder.textViewRight.setText(Integer.valueOf(currentItem.getFileSize()) / 1000 + " KB");
                holder.imageViewFileRight.setVisibility(View.VISIBLE);
                holder.imageViewFileRight.setOnClickListener(onClickListener);
                holder.cardViewRight.setVisibility(View.VISIBLE);
            } else {
                holder.textViewLeft.setText(Integer.valueOf(currentItem.getFileSize()) / 1000 + " KB");
                holder.imageViewFileLeft.setVisibility(View.VISIBLE);
                holder.imageViewFileLeft.setOnClickListener(onClickListener);
                holder.cardViewLeft.setVisibility(View.VISIBLE);
            }
        } else if (currentItem.getTYPE().equals("IMAGE")) {
            if (User.getInstance().getUuid().equals(currentItem.getSenderId())) {
                holder.textViewRight.setVisibility(View.GONE);
                holder.imageRight.setVisibility(View.VISIBLE);
                if (currentItem.getImageUri() != null) {
                    Picasso.with(holder.imageRight.getContext())
                            .load(currentItem.getImageUri())
                            .fit().centerCrop()
                            .into(holder.imageRight);
                    holder.cardViewRight.setVisibility(View.VISIBLE);
                }
            } else {
                holder.textViewLeft.setVisibility(View.GONE);
                holder.imageLeft.setVisibility(View.VISIBLE);
                if (currentItem.getImageUri() != null) {
                    Picasso.with(holder.imageLeft.getContext())
                            .load(currentItem.getImageUri())
                            .fit().centerCrop()
                            .into(holder.imageLeft);
                    holder.cardViewLeft.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("TIKLANDIM AQ");
        }
    };

    @Override
    public int getItemCount() {
        return mModelList.size();
    }
}
