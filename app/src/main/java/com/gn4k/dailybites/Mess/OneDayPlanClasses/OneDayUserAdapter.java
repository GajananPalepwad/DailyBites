package com.gn4k.dailybites.Mess.OneDayPlanClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gn4k.dailybites.Animatin.LoadingDialog;
import com.gn4k.dailybites.HomeForMessOwner;
import com.gn4k.dailybites.Mess.QrCodeGenerator;
import com.gn4k.dailybites.Mess.consumersUserlistFragment.UserModelForMess;
import com.gn4k.dailybites.R;

import java.util.ArrayList;

public class OneDayUserAdapter extends RecyclerView.Adapter<OneDayUserAdapter.MyViewHolder>  {

    Context context;
    ArrayList<OneDayUserModel> list;

    public interface OnItemClickListener {
        void onItemClick(OneDayUserModel model);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    public OneDayUserAdapter(Context context,  ArrayList<OneDayUserModel> list) {
        this.context = context;
        this.list = list;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.one_day_user_list,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OneDayUserModel model = list.get(position);
        holder.userName.setText(model.name);
        holder.time.setText(model.time);
        holder.mobileNo.setText(model.mobileNo);

        if(model.delivery.equals("no")){
            holder.map.setVisibility(View.GONE);
        }

        holder.qr.setOnClickListener(v -> {
            Intent intent = new Intent(context, QrCodeGenerator.class);
            intent.putExtra("qr_data", model.orderId);
            context.startActivity(intent);
        });

        holder.map.setOnClickListener(v -> {
            openGoogleMaps(model.latitude, model.longitude);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(model);
            }
        });


    }

    private void openGoogleMaps(String latitude, String longitude) {
        String uri = "https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userName, mobileNo, time;
        ImageView qr, map;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            mobileNo = itemView.findViewById(R.id.mobileNo);
            time = itemView.findViewById(R.id.time);
            qr = itemView.findViewById(R.id.show_qr);
            map = itemView.findViewById(R.id.show_map);


        }
    }
}
