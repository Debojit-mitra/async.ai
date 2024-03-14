package com.bunny.ai.asyncai.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.activities.EditProfileActivity;
import com.bunny.ai.asyncai.activities.ExpertsAllActivity;
import com.bunny.ai.asyncai.activities.MainActivity;
import com.bunny.ai.asyncai.models.MessageReadWrite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageReadWrite> messageArrayList;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    final int ITEM_SENT = 0;
    final int ITEM_RECEIVE = 1;
    boolean isTyping = false;

    public MessagesAdapter(Context context, ArrayList<MessageReadWrite> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat_layout, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_chat_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        MessageReadWrite message = messageArrayList.get(position);
        //checking if user is the sender to sent message in the activity
        assert firebaseUser != null;
        if (firebaseUser.getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageReadWrite message = messageArrayList.get(position);
        //to stop messages from duplicating
        holder.setIsRecyclable(false);

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            //setting sender message
            viewHolder.textView_senderMessage.setText(messageArrayList.get(holder.getLayoutPosition()).getMessage());
            //converting timestamp to date
            Date d = new Date(message.getTimestamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yy ");
            String date = dateFormat.format(d);
            viewHolder.textView_senderMessage_time.setText(date);

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            //setting receiver message
            if(message.getMessage().equals("typing")){
                viewHolder.textView_receiverMessage.setText(message.getMessage());
            } else {
                viewHolder.layout_for_typing.setVisibility(View.GONE);
                viewHolder.layoutForMessage.setVisibility(View.VISIBLE);
                viewHolder.textView_receiverMessage.setText(message.getMessage());
                //converting timestamp to date
                Date d = new Date(message.getTimestamp());
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yy ");
                String date = dateFormat.format(d);
                viewHolder.textView_receiverMessage_time.setText(date);
                viewHolder.textView_notSatisfied.setVisibility(View.VISIBLE);
                viewHolder.textView_notSatisfied.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ExpertsAllActivity.class);
                        context.startActivity(intent);
                        ((Activity) viewHolder.textView_notSatisfied.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }


            //setting receiver role and name
            String isBot = message.getIsBot();
            String isExpert = message.getIsExpert();

            if (isBot.equals("YES")) {
                String receiverRoleAndName = "@Bot : " + message.getName();
                viewHolder.textView_receiverRoleAndName.setText(receiverRoleAndName);
            } else if (isExpert.equals("YES")) {
                String receiverRoleAndName = "@Pro : " + message.getName();
                viewHolder.textView_receiverRoleAndName.setText(receiverRoleAndName);
            }

        }

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {
        TextView textView_senderMessage, textView_senderMessage_time;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_senderMessage = itemView.findViewById(R.id.textView_senderMessage);
            textView_senderMessage_time = itemView.findViewById(R.id.textView_senderMessage_time);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView textView_receiverMessage, textView_receiverMessage_time, textView_receiverRoleAndName, textView_notSatisfied;
        LinearLayout layout_for_typing, layoutForMessage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_receiverMessage = itemView.findViewById(R.id.textView_receiverMessage);
            textView_receiverMessage_time = itemView.findViewById(R.id.textView_receiverMessage_time);
            textView_receiverRoleAndName = itemView.findViewById(R.id.textView_receiverRoleAndName);
            layoutForMessage = itemView.findViewById(R.id.layoutForMessage);
            layout_for_typing = itemView.findViewById(R.id.layout_for_typing);
            textView_notSatisfied = itemView.findViewById(R.id.textView_notSatisfied);

        }
    }

}
