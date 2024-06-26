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
import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.User.MessInfo;

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
        this.loadingDialog = loadingDialog;

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
        Glide.with(context).load(messmodel.getCoverImage()).centerCrop().placeholder(R.drawable.indian_food_art).into(holder.coverImg);
        if(messmodel.getIsVerified().equals("yes")) {
            holder.ver.setText("Verified");
        }else{
            holder.ver.setText("Not Verified");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.startLoading();
                ValuesLocal.show = false;
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


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {///////////////////////////////////////////////////////////////
        double R = 6371; // Radius of the earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in kilometers
        return distance;
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView messName, ver;
        ImageView coverImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messName = itemView.findViewById(R.id.messName);
            coverImg = itemView.findViewById(R.id.coverImg);
            ver = itemView.findViewById(R.id.ver);


        }
    }
}
