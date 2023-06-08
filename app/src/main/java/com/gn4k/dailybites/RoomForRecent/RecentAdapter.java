package com.gn4k.dailybites.RoomForRecent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.R;

import java.util.List;

// MessAdapter class
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyRecentViewHolder> {


    List<Mess> messList;
    ViewGroup parent;
    public RecentAdapter(List<Mess> messList) {
        this.messList = messList;
    }



    static class MyRecentViewHolder extends RecyclerView.ViewHolder{

        TextView messName;
        ImageView cover;
        public MyRecentViewHolder(@NonNull View itemView) {
            super(itemView);
            messName = itemView.findViewById(R.id.messName);
            cover = itemView.findViewById(R.id.coverImg);
        }


    }


    @NonNull
    @Override
    public MyRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messlistcards, parent,false);
        this.parent = parent;
        return new MyRecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecentViewHolder holder, int position) {
        holder.messName.setText(messList.get(position).getMessNameR());
        Glide.with(parent.getContext()).load(messList.get(position).getUrlCover()).centerCrop().placeholder(R.drawable.silver).into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

}
