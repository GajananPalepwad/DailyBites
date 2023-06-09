package com.gn4k.dailybites;

import android.app.Activity;
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
import com.gn4k.dailybites.Animatin.LoadingDialog;

import java.util.ArrayList;

public class MyMessAdapterForHome extends RecyclerView.Adapter<MyMessAdapterForHome.MyViewHolder>  {

    Context context;
    private Activity activity;
    private LoadingDialog loadingDialog;
    ArrayList<MessModel> list;

    public MyMessAdapterForHome(Context context, Activity activity, LoadingDialog loadingDialog, ArrayList<MessModel> list) {
        this.context = context;
        this.activity = activity;
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
        Glide.with(context).load(messmodel.getCoverImage()).centerCrop().placeholder(R.drawable.silver).into(holder.coverImg);
        loadingDialog = new LoadingDialog(activity);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoading();

                // Handle card click event
                // You can start a new activity or perform any desired action here
                if (messmodel.getLatitude() != null && messmodel.getLongitude() != null) {

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

                } else {

                    // Handle the scenario when latitude and longitude are not found
                    // For example, display a toast message indicating the unavailability of location information
                    Toast.makeText(context, "Location not available for " + messmodel.messName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MessInfo.class);
                    intent.putExtra("messLatitude", "");
                    intent.putExtra("messLongitude", "");
                    intent.putExtra("messMobile", messmodel.getMobileNo());
                    intent.putExtra("messName", messmodel.getMessName());
                    context.startActivity(intent);

                }

            }

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView messName;
        ImageView coverImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messName = itemView.findViewById(R.id.messName);
            coverImg = itemView.findViewById(R.id.coverImg);


        }
    }
}
