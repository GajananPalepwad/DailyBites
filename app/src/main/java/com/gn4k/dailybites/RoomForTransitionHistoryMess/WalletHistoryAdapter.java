package com.gn4k.dailybites.RoomForTransitionHistoryMess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.MessInfo;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.RoomForRecent.Mess;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

// MessAdapter class
public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.MyRecentViewHolder> {


    List<WalletMess> messList;
    Context context;
    ViewGroup parent;
    public WalletHistoryAdapter(Context context, List<WalletMess> messList) {
        this.context = context;
        this.messList = messList;
    }


    static class MyRecentViewHolder extends RecyclerView.ViewHolder {
        TextView time, status, amount;
        ImageView StatusImg;
        CardView cardView;

        MyRecentViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.dateTime);
            status = itemView.findViewById(R.id.penOrCom);
            amount = itemView.findViewById(R.id.amount);
            StatusImg = itemView.findViewById(R.id.coverImg);
            cardView = itemView.findViewById(R.id.cardViewWallet);

        }

    }



    @NonNull
    @Override
    public MyRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent; // Set the parent variable to the provided parent
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transition_history_card, parent, false);
        return new MyRecentViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyRecentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.time.setText(messList.get(position).getTime());
        holder.amount.setText(messList.get(position).getAmount());
        holder.status.setText(messList.get(position).getStatus());

        if(messList.get(position).getStatus().equals("Pending")) {
            holder.StatusImg.setImageResource(R.drawable.pending_payment);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.FentYellow));
        } else if (messList.get(position).getStatus().equals("Completed")) {
            holder.StatusImg.setImageResource(R.drawable.done_payment);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.FentGreen));
        }


    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

}
