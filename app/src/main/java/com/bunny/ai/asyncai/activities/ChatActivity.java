package com.bunny.ai.asyncai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.adapter.MessagesAdapter;
import com.bunny.ai.asyncai.models.ChatReadWriteDetails;
import com.bunny.ai.asyncai.models.MessageReadWrite;
import com.bunny.ai.asyncai.utils.KeyboardUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    TextView textView_chat_number, select_chat_type;
    ImageButton back_btn, main_chat_send_btn;
    String newChatButtonPressed;
    RecyclerView recyclerView_messages;
    EditText editMessage;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    String defaultCommand = "!ask", totalChats, savedName, messageTxt, solved;
    ArrayList<MessageReadWrite> messageReadWriteArrayList;
    int totalChatsHere;
    long totalC;
    String chatNoNameFromIntent, solvedFromIntent;
    MessagesAdapter messagesAdapter;
    String messageIdSaved, responsed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        textView_chat_number = findViewById(R.id.textView_chat_number);
        back_btn = findViewById(R.id.back_btn);
        main_chat_send_btn = findViewById(R.id.main_chat_send_btn);
        select_chat_type = findViewById(R.id.select_chat_type);
        recyclerView_messages = findViewById(R.id.recyclerView_messages);
        editMessage = findViewById(R.id.editMessage);
        newChatButtonPressed = getIntent().getStringExtra("newChatButtonPressed");
        savedName = getIntent().getStringExtra("name");

        if (!newChatButtonPressed.contains("YES")) {
            chatNoNameFromIntent = getIntent().getStringExtra("Name");
            solvedFromIntent = getIntent().getStringExtra("solved");
        }

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        messageReadWriteArrayList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messageReadWriteArrayList);

        recyclerView_messages.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_messages.setHasFixedSize(false);
        recyclerView_messages.setAdapter(messagesAdapter);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        getChatNoAndLoadOldChats();
        commandPopupMenu();

        main_chat_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        //used to pull up the recyclerview
        KeyboardUtils.addKeyboardToggleListener(ChatActivity.this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                recyclerView_messages.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        });
    }

    private void sendMessage() {
        messageTxt = editMessage.getText().toString().trim();
        if (!messageTxt.equals("")) {
            editMessage.setText("");
            if(!defaultCommand.equals("Chat")){
                messageTxt = defaultCommand + " " + messageTxt;
            }


            DatabaseReference referenceProfile = firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("totalChats");
            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (newChatButtonPressed.contains("YES")) {
                        if (snapshot.exists()) {
                            Log.e("here1", "here1");
                            totalChats = snapshot.getValue(String.class);
                            if (totalChats != null) {
                                totalC = Long.parseLong(totalChats);

                                totalC++;
                                chatNoNameFromIntent = String.valueOf(totalC);
                                referenceProfile.setValue(String.valueOf(totalC));

                                Date date = new Date();
                                solved = "no";
                                String members = firebaseUser.getUid() + ", async.aiBot";
                                ChatReadWriteDetails chatReadWriteDetails = new ChatReadWriteDetails(members, solved, String.valueOf(totalC), String.valueOf(date.getTime()));

                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid())
                                        .child("chats").child(String.valueOf(totalC));
                                databaseReference.setValue(chatReadWriteDetails).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ChatActivity.this, "Message sent failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                createNewMessageSpace((int) totalC, messageTxt);
                                LoadBotReply((int) totalC,messageTxt);
                                messageTxt = "";
                            }
                        } else {
                            Log.e("here2", "here2");
                            referenceProfile.setValue("1");
                            totalC = 0;
                            totalC++;
                            chatNoNameFromIntent = String.valueOf(totalC);
                            Date date = new Date();
                            solved = "no";
                            String members = firebaseUser.getUid() + ", async.aiBot";
                            ChatReadWriteDetails chatReadWriteDetails = new ChatReadWriteDetails(members, solved, String.valueOf(totalC), String.valueOf(date.getTime()));

                            DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid())
                                    .child("chats").child(String.valueOf(totalC));
                            databaseReference.setValue(chatReadWriteDetails).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChatActivity.this, "Message sent failed!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            createNewMessageSpace((int) totalC, messageTxt);
                            LoadBotReply((int) totalC, messageTxt);
                            messageTxt = "";
                        }
                    } else {
                        Log.e("here3", "here3");

                            createNewMessageSpace(Integer.parseInt(chatNoNameFromIntent), messageTxt);
                            LoadBotReply(Integer.parseInt(chatNoNameFromIntent), messageTxt);
                            messageTxt = "";

                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

    }

    private void LoadBotReply(int totalChatsRec, String messageTxt) {

        totalChatsHere = totalChatsRec;
        System.out.println("totalChatsHere: " + totalChatsHere);
        Date date = new Date();
        String messageType = "text";
        MessageReadWrite messageReadWrite = new MessageReadWrite("typing", "async.aiBot", "async.aiBot", "NO", "YES", "NO", messageType, date.getTime());
        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).push()
                .setValue(messageReadWrite).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        newChatButtonPressed = "NO";

                        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageReadWriteArrayList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String childKey = dataSnapshot.getKey();
                                    if (childKey != null && childKey.contains("created") || childKey != null && childKey.contains("members")
                                            || childKey != null && childKey.contains("name") || childKey != null && childKey.contains("solved") || childKey != null && childKey.contains("lastMessage")) {
                                        continue;
                                    }
                                    MessageReadWrite message = dataSnapshot.getValue(MessageReadWrite.class);
                                    assert message != null;
                                    message.setMessageId(dataSnapshot.getKey());
                                    messageIdSaved = message.getMessageId();
                                    messageReadWriteArrayList.add(message);
                                }
                                notifyAdapter();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

        recyclerView_messages.smoothScrollToPosition(messageReadWriteArrayList.size());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);

                String botURL = "https://a65f-14-139-209-82.ngrok-free.app/ai";
                String rawJsonData = "{\"msg\":\"" + messageTxt + "\"}";


                StringRequest stringRequest = new StringRequest(Request.Method.POST, botURL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        response = response.substring(response.indexOf("\":\"")+3, response.indexOf("\"}"));
                        System.out.println(response);
                        responsed = response;

                        totalChatsHere = totalChatsRec;
                        System.out.println("totalChatsHere: " + totalChatsHere);
                        Date date = new Date();
                        String messageType = "text";
                        MessageReadWrite messageReadWrite = new MessageReadWrite(response, "async.aiBot", "async.aiBot", "NO", "YES", "NO", messageType, date.getTime());
                        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).child(messageIdSaved)
                                .setValue(messageReadWrite).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        newChatButtonPressed = "NO";

                                        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).child("lastMessage").setValue(responsed);

                                        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                messageReadWriteArrayList.clear();
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    String childKey = dataSnapshot.getKey();
                                                    if (childKey != null && childKey.contains("created") || childKey != null && childKey.contains("members")
                                                            || childKey != null && childKey.contains("name") || childKey != null && childKey.contains("solved") || childKey != null && childKey.contains("lastMessage")) {
                                                        continue;
                                                    }
                                                    MessageReadWrite message = dataSnapshot.getValue(MessageReadWrite.class);
                                                    assert message != null;
                                                    message.setMessageId(dataSnapshot.getKey());
                                                    messageReadWriteArrayList.add(message);
                                                }
                                                notifyAdapter();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        // Convert your raw JSON data to a byte array
                        return rawJsonData.getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        // Set the content type to JSON
                        return "application/json";
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here
            });
        });


    }

    private void createNewMessageSpace(int totalChatsRec, String messageTxt) {
        totalChatsHere = totalChatsRec;
        System.out.println("totalChatsHere: " + totalChatsHere);
        Date date = new Date();
        String messageType = "text";
        MessageReadWrite messageReadWrite = new MessageReadWrite(messageTxt, firebaseUser.getUid(), savedName, "YES", "NO", "NO", messageType, date.getTime());
        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).push()
                .setValue(messageReadWrite).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        newChatButtonPressed = "NO";

                        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).child("lastMessage").setValue(messageTxt);

                        firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(String.valueOf(totalChatsHere)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messageReadWriteArrayList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String childKey = dataSnapshot.getKey();
                                    if (childKey != null && childKey.contains("created") || childKey != null && childKey.contains("members")
                                            || childKey != null && childKey.contains("name") || childKey != null && childKey.contains("solved")
                                            || childKey != null && childKey.contains("lastMessage")) {
                                        continue;
                                    }
                                    MessageReadWrite message = dataSnapshot.getValue(MessageReadWrite.class);
                                    assert message != null;
                                    message.setMessageId(dataSnapshot.getKey());
                                    messageReadWriteArrayList.add(message);
                                }
                                notifyAdapter();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
    }

    private void notifyAdapter() {
        messagesAdapter.notifyDataSetChanged();
        recyclerView_messages.scrollToPosition(messagesAdapter.getItemCount() - 1);
        //recyclerView_messages.smoothScrollToPosition(messageReadWriteArrayList.size());
       // messagesAdapter.notifyItemInserted(messageReadWriteArrayList.size());
        //messagesAdapter.notifyItemInserted(messagesAdapter.getItemCount() + 1);
        //messagesAdapter.notifyItemChanged(messagesAdapter.getItemCount() - 1);
        //recyclerView_messages.scrollToPosition(messagesAdapter.getItemCount() - 1);
    }

    private void commandPopupMenu() {

        select_chat_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(ChatActivity.this, select_chat_type);
                popup.getMenuInflater().inflate(R.menu.menu_popup_command, popup.getMenu());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.ask) {
                            if (!defaultCommand.contains("!ask")) {
                                defaultCommand = "!ask";
                                select_chat_type.setText(defaultCommand);

                            }
                        } else if (menuItem.getItemId() == R.id.summarize) {
                            if (!defaultCommand.contains("!summarize")) {
                                defaultCommand = "!summarize";
                                select_chat_type.setText(defaultCommand);

                            }
                        } /*else if (menuItem.getItemId() == R.id.wiki) {
                            if (!defaultCommand.contains("!wiki")) {
                                defaultCommand = "!wiki";
                                select_chat_type.setText(defaultCommand);

                            }
                        }*/ else if (menuItem.getItemId() == R.id.Chat) {
                            if (!defaultCommand.contains("Chat")) {
                                defaultCommand = "Chat";
                                select_chat_type.setText(defaultCommand);

                            }
                        }

                        return false;
                    }
                });
                popup.show();

            }
        });
    }

    private void getChatNoAndLoadOldChats() {
        if (newChatButtonPressed != null && newChatButtonPressed.contains("YES")) {

            firebaseDatabase.getReference().child("users").child(firebaseUser.getUid())
                    .child("totalChats").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String textChat = "Chat No. ";
                            if (snapshot.exists()) {
                                int textChatInt = Integer.parseInt(Objects.requireNonNull(snapshot.getValue(String.class)));
                                textChatInt++;
                                textChat = textChat + textChatInt;
                                textView_chat_number.setText(textChat);
                            } else {
                                textChat = textChat + 1;
                                textView_chat_number.setText(textChat);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            // firebaseDatabase.getReference().child(firebaseUser.getUid()).child("Chats").

        } else if (newChatButtonPressed != null && !newChatButtonPressed.contains("YES") && solvedFromIntent != null) {
            String textChat = "Chat No. " + chatNoNameFromIntent;
            textView_chat_number.setText(textChat);

            firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("chats").child(chatNoNameFromIntent).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String childKey = dataSnapshot.getKey();
                        if (childKey != null && childKey.contains("created") || childKey != null && childKey.contains("members")
                                || childKey != null && childKey.contains("name") || childKey != null && childKey.contains("solved")
                                || childKey != null && childKey.contains("lastMessage")) {
                            continue;
                        }
                        MessageReadWrite message = dataSnapshot.getValue(MessageReadWrite.class);
                        assert message != null;
                        message.setMessageId(dataSnapshot.getKey());
                        messageReadWriteArrayList.add(message);

                    }
                    notifyAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}