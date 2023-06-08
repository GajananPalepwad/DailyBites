package com.gn4k.dailybites.RoomForRecent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.MessInfo;
import com.gn4k.dailybites.R;

import java.util.List;

// MessAdapter class
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyRecentViewHolder> {


    List<Mess> messList;
    Context context;
    ViewGroup parent;
    public RecentAdapter(Context context, List<Mess> messList) {
        this.context = context;
        this.messList = messList;
    }

    private static OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    static class MyRecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messName;
        ImageView cover;

        MyRecentViewHolder(@NonNull View itemView) {
            super(itemView);
            messName = itemView.findViewById(R.id.messName);
            cover = itemView.findViewById(R.id.coverImg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }



    @NonNull
    @Override
    public MyRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent; // Set the parent variable to the provided parent
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messlistcards, parent, false);
        return new MyRecentViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyRecentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.messName.setText(messList.get(position).getMessNameR());
        Glide.with(parent.getContext()).load(messList.get(position).getUrlCover()).centerCrop().placeholder(R.drawable.silver).into(holder.cover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessInfo.class);
                intent.putExtra("messMobile", messList.get(position).getMessNoR());
                intent.putExtra("messName", messList.get(position).getMessNameR());
                intent.putExtra("messLatitude", "");
                intent.putExtra("messLongitude", "");
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

}
