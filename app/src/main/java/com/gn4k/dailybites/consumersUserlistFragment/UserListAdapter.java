package com.gn4k.dailybites.consumersUserlistFragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.gn4k.dailybites.MessInfo;
import com.gn4k.dailybites.MessModel;
import com.gn4k.dailybites.R;


import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder>  {

    Context context;
    private Activity activity;
    private LoadingDialog loadingDialog;
    ArrayList<UserModelForMess> list;

    public UserListAdapter(Context context, Activity activity, LoadingDialog loadingDialog, ArrayList<UserModelForMess> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.loadingDialog = loadingDialog;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_list,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModelForMess model = list.get(position);
        holder.userName.setText(model.name);
        holder.to.setText(model.toDate);
        holder.from.setText(model.fromDate);
        holder.mobileNo.setText(model.mobileNo);

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userName, mobileNo, from, to;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            mobileNo = itemView.findViewById(R.id.mobileNo);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);


        }
    }
}
