package com.bunny.ai.asyncai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.adapter.ChatsAdapter;
import com.bunny.ai.asyncai.models.ChatReadWriteDetails;
import com.bunny.ai.asyncai.models.MessageReadWrite;
import com.bunny.ai.asyncai.models.ReadWriteUserDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_show;
    FloatingActionButton add_new_chat_btn;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    String profileImage, name;
    ArrayList<ChatReadWriteDetails> chatReadWriteDetailsArrayList;
    ChatsAdapter chatsAdapter;
    RecyclerView recyclerView_recentChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_show = findViewById(R.id.profile_show);
        add_new_chat_btn = findViewById(R.id.add_new_chat_btn);
        recyclerView_recentChats = findViewById(R.id.recyclerView_recentChats);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatReadWriteDetailsArrayList = new ArrayList<>();
        chatsAdapter = new ChatsAdapter(MainActivity.this, chatReadWriteDetailsArrayList);

        profile_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        LoadProfileImage();

        add_new_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("newChatButtonPressed", "YES");
                intent.putExtra("name", name);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        DatabaseReference referenceProfile = firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("chats");
        referenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatReadWriteDetailsArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatReadWriteDetails chatReadWriteDetails = dataSnapshot.getValue(ChatReadWriteDetails.class);
                    assert chatReadWriteDetails != null;
                    chatReadWriteDetails.setLastMessage(dataSnapshot.child("lastMessage").getValue(String.class));
                    chatReadWriteDetailsArrayList.add(chatReadWriteDetails);
                }
               // chatReadWriteDetails.setLastMessage();
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView_recentChats.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView_recentChats.setAdapter(chatsAdapter);


    }

    private void LoadProfileImage() {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
        referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null) {
                    profileImage = readWriteUserDetails.profileImage;
                    name = readWriteUserDetails.fullName;
                    if (!profileImage.equals("n/a")) {
                        Glide.with(MainActivity.this).load(profileImage)
                                .placeholder(R.drawable.ic_profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profile_show);
                    }
                    referenceProfile.keepSynced(true);


                } else {
                    Toast.makeText(MainActivity.this, "Error loading profile image!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading profile image!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}