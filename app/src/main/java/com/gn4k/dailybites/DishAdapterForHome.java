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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.Animatin.LoadingDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class DishAdapterForHome extends RecyclerView.Adapter<DishAdapterForHome.MyViewHolder>  {

    Context context;
    private Activity activity;
    private LoadingDialog loadingDialog;
    ArrayList<MessModel> list;
    View view;
    LayoutInflater inflaterH;

    public DishAdapterForHome(Context context, Activity activity, LoadingDialog loadingDialog, ArrayList<MessModel> list, View view, LayoutInflater inflaterH) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.loadingDialog = loadingDialog;
        this.view = view;
        this.inflaterH = inflaterH;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.dishlistcards,parent,false);
        return  new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull DishAdapterForHome.MyViewHolder holder, int position) {

        MessModel messmodel = list.get(position);
        holder.menu.setText(messmodel.getToDayDish());
        holder.dishprice.setText("₹"+messmodel.getDishPrize());
        holder.ratings.setText(messmodel.getRatings());
        Glide.with(context).load(messmodel.getCoverImage()).centerCrop().placeholder(R.drawable.silver).into(holder.coverImg);
        if(messmodel.getIsVerified().equals("yes")) {
            holder.ver.setText("Verified");
        }else{
            holder.ver.setText("Not Verified");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSettingsBottomSheetDialog();

            }

        });


    }



    private BottomSheetDialog bottomSheetDialog;
    private void showSettingsBottomSheetDialog() {

        // Inflate the layout for the BottomSheetDialog
        View bottomSheetView = inflaterH.inflate(R.layout.dishlist_bottomsheet, (ConstraintLayout) view.findViewById(R.id.setting_sheet));

        bottomSheetDialog = new BottomSheetDialog(activity,R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView menu, ver, dishprice, ratings;
        ImageView coverImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            menu = itemView.findViewById(R.id.menu);
            coverImg = itemView.findViewById(R.id.coverImg);
            ver = itemView.findViewById(R.id.isVerified);
            dishprice = itemView.findViewById(R.id.price);
            ratings = itemView.findViewById(R.id.ratings);


        }
    }
}
