package com.gn4k.dailybites;

import android.content.Context;
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
