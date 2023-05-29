package com.gn4k.dailybites;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyMessAdapterForHome extends RecyclerView.Adapter<MyMessAdapterForHome.MyViewHolder>  {

    Context context;

    ArrayList<MessModel> list;

    public MyMessAdapterForHome(Context context, ArrayList<MessModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.messlistcards,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMessAdapterForHome.MyViewHolder holder, int position) {

        MessModel messmodel = list.get(position);
        holder.messName.setText(messmodel.getMessName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle card click event
                // You can start a new activity or perform any desired action here
                // Convert latitude and longitude to strings before setting them
                String latitude = String.valueOf(messmodel.getLatitude());
                String longitude = String.valueOf(messmodel.getLongitude());

                // Set the converted latitude and longitude strings to the intent extras
                Intent intent = new Intent(context, MessInfo.class);
                intent.putExtra("messMobile", messmodel.getMobileNo());
                intent.putExtra("messName", messmodel.getMessName());
                intent.putExtra("messLatitude", latitude);
                intent.putExtra("messLongitude", longitude);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView messName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messName = itemView.findViewById(R.id.messName);


        }
    }
}
