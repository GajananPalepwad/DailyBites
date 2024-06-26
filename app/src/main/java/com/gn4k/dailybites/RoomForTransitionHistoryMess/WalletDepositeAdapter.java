package com.gn4k.dailybites.RoomForTransitionHistoryMess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gn4k.dailybites.R;

import java.util.List;

// MessAdapter class
public class WalletDepositeAdapter extends RecyclerView.Adapter<WalletDepositeAdapter.MyRecentViewHolder> {


    List<WalletMess> messList;
    Context context;
    ViewGroup parent;
    public WalletDepositeAdapter(Context context, List<WalletMess> messList) {
        this.context = context;
        this.messList = messList;
    }


    static class MyRecentViewHolder extends RecyclerView.ViewHolder {
        TextView time, status, amount, title;
        ImageView StatusImg;
        CardView cardView;

        MyRecentViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.dateTime);
            status = itemView.findViewById(R.id.penOrCom);
            amount = itemView.findViewById(R.id.amount);
            StatusImg = itemView.findViewById(R.id.coverImg);
            cardView = itemView.findViewById(R.id.cardViewWallet);
            title = itemView.findViewById(R.id.userName);
        }

    }



    @NonNull
    @Override
    public MyRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent; // Set the parent variable to the provided parent
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposite_transition_history_card, parent, false);
        return new MyRecentViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyRecentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.time.setText(messList.get(position).getTime());
        holder.amount.setText(messList.get(position).getAmount());
        holder.status.setText(messList.get(position).getStatus());

        if(messList.get(position).getStatus().equals("Under Review")) {
            holder.StatusImg.setImageResource(R.drawable.pending_payment);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.FentYellow));
        } else if (messList.get(position).getStatus().equals("Completed")) {
            holder.StatusImg.setImageResource(R.drawable.done_payment);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.FentGreen));
        } else if (messList.get(position).getStatus().equals("Rejected")) {
            holder.StatusImg.setImageResource(R.drawable.rejected_payment);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.FentRed));
        }

        if(messList.get(position).getStatus().equals("Plan Subscribed")){
            holder.title.setText("INR Deducted");
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.FentBlue));
        }



    }

    public void updateData(List<WalletMess> newData) {
        this.messList = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

}
