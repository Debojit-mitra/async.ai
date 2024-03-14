package com.bunny.ai.asyncai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.activities.MainActivity;
import com.bunny.ai.asyncai.models.ExpertList;

import java.util.ArrayList;

public class ExpertsAdapter extends RecyclerView.Adapter<ExpertsAdapter.ExpertsViewHolder> {

    Context context;
    ArrayList<ExpertList> expertListArrayList;

    public ExpertsAdapter(Context context, ArrayList<ExpertList> expertListArrayList) {
        this.context = context;
        this.expertListArrayList = expertListArrayList;
    }

    @NonNull
    @Override
    public ExpertsAdapter.ExpertsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expert_layout, parent, false);
        return new ExpertsAdapter.ExpertsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpertsAdapter.ExpertsViewHolder holder, int position) {

        Glide.with(holder.person_imageView.getContext()).load(expertListArrayList.get(position).getImage())
                .placeholder(R.drawable.ic_profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.person_imageView);

        holder.show_name.setText(expertListArrayList.get(position).getName());
        holder.show_desc.setText(expertListArrayList.get(position).getDesc());

    }

    @Override
    public int getItemCount() {
        return expertListArrayList.size();
    }

    public class ExpertsViewHolder extends RecyclerView.ViewHolder {
        ImageView person_imageView;
        TextView show_name, show_desc;
        ProgressBar progressBar;
        public ExpertsViewHolder(@NonNull View itemView) {
            super(itemView);
            person_imageView = itemView.findViewById(R.id.person_imageView);
            show_name = itemView.findViewById(R.id.show_name);
            show_desc = itemView.findViewById(R.id.show_desc);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
