package com.bunny.ai.asyncai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.adapter.ChatsAdapter;
import com.bunny.ai.asyncai.adapter.ExpertsAdapter;
import com.bunny.ai.asyncai.models.ExpertList;

import java.util.ArrayList;

public class ExpertsAllActivity extends AppCompatActivity {

    ArrayList<ExpertList> expertListArrayList;
    ExpertsAdapter expertsAdapter;
    RecyclerView recyclerView_experts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experts_all);

        recyclerView_experts = findViewById(R.id.recyclerView_experts);

        expertListArrayList = new ArrayList<>();
        expertListArrayList.add(new ExpertList("Rohit Chauhan", "C++, Java, Python","https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"));
        expertListArrayList.add(new ExpertList("Tanu Sharma", "TypeScript, Python","https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"));
        expertListArrayList.add(new ExpertList("Molu Raj", "C#, Java","https://images.pexels.com/photos/343717/pexels-photo-343717.jpeg?auto=compress&cs=tinysrgb&w=1600"));
        expertListArrayList.add(new ExpertList("Niraj Mishra", "C++, C, Java, Python","https://images.pexels.com/photos/2076596/pexels-photo-2076596.jpeg?auto=compress&cs=tinysrgb&w=1600"));

        expertsAdapter = new ExpertsAdapter(ExpertsAllActivity.this, expertListArrayList);
        recyclerView_experts.setLayoutManager(new LinearLayoutManager(ExpertsAllActivity.this));
        recyclerView_experts.setAdapter(expertsAdapter);
        expertsAdapter.notifyDataSetChanged();

    }
}