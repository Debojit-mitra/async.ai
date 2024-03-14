package com.bunny.ai.asyncai.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.activities.ChatActivity;
import com.bunny.ai.asyncai.models.ChatReadWriteDetails;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    Context context;
    ArrayList<ChatReadWriteDetails> chatReadWriteDetailsArrayList;

    public ChatsAdapter(Context context, ArrayList<ChatReadWriteDetails> chatReadWriteDetailsArrayList) {
        this.context = context;
        this.chatReadWriteDetailsArrayList = chatReadWriteDetailsArrayList;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view_layout, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.ChatsViewHolder holder, int position) {

        ChatReadWriteDetails chatReadWriteDetails = chatReadWriteDetailsArrayList.get(position);
        String chatNo = "Chat No. " + chatReadWriteDetails.getName();
        holder.chatView_requestNo.setText(chatNo);

        holder.recyclerView_relative_chatViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Name", chatReadWriteDetails.getName());
                intent.putExtra("solved", chatReadWriteDetails.getSolved());
                intent.putExtra("newChatButtonPressed", "NO");
                context.startActivity(intent);
                ((Activity) holder.recyclerView_relative_chatViewbtn.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        holder.chatView_lastChat.setText(chatReadWriteDetailsArrayList.get(position).getLastMessage());

    }

    @Override
    public int getItemCount() {
        return chatReadWriteDetailsArrayList.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder {
        TextView chatView_requestNo, chatView_lastChat;
        ProgressBar progressBarBs;
        RelativeLayout recyclerView_relative_chatViewbtn;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            chatView_requestNo = itemView.findViewById(R.id.chatView_requestNo);
            chatView_lastChat = itemView.findViewById(R.id.chatView_lastChat);
            recyclerView_relative_chatViewbtn = itemView.findViewById(R.id.recyclerView_relative_chatViewbtn);
            progressBarBs = itemView.findViewById(R.id.progressBarBs);
        }
    }
}
