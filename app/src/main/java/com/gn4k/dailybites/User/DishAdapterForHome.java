package com.gn4k.dailybites.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.MessModel;
import com.gn4k.dailybites.R;

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
        if(messmodel.getMenu().equals("") || messmodel.getMenu()==null) {
            ViewGroup.LayoutParams layoutParams = holder.card.getLayoutParams();
            layoutParams.height = 0;
            holder.card.setLayoutParams(layoutParams);
        }

        holder.menu.setText(messmodel.getMenu());
        holder.dishprice.setText("₹"+messmodel.getDishPrize());
        holder.ratings.setText(messmodel.getRatings());
        holder.myRating.setRating(Float. parseFloat(messmodel.getRatings()));
        Glide.with(context).load(messmodel.getCoverImage()).centerCrop().placeholder(R.drawable.indian_food_art).into(holder.coverImg);
        if(messmodel.getIsVerified().equals("yes")) {
            holder.ver.setText("Verified");
        }else{
            holder.ver.setText("Not Verified");
        }
        holder.itemView.setOnClickListener(v -> {

            String latitude = String.valueOf(messmodel.getLatitude());
            String longitude = String.valueOf(messmodel.getLongitude());

            Intent intent = new Intent(context, DishInfo.class);
            intent.putExtra("messMobile", messmodel.getMobileNo());
            intent.putExtra("messName", messmodel.getMessName());
            intent.putExtra("messIsDelivery", messmodel.getIsDelivery());
            intent.putExtra("messCoverImage", messmodel.getCoverImage());
            intent.putExtra("messRatings", messmodel.getRatings());
            intent.putExtra("messDishPrize", messmodel.getDishPrize());
            intent.putExtra("messToDayDish", messmodel.getMenu());
            intent.putExtra("messLatitude", latitude);
            intent.putExtra("messLongitude", longitude);

            context.startActivity(intent);

        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView menu, ver, dishprice, ratings;
        RatingBar myRating;
        CardView card;
        ImageView coverImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            menu = itemView.findViewById(R.id.menu);
            coverImg = itemView.findViewById(R.id.coverImg);
            ver = itemView.findViewById(R.id.isVerified);
            dishprice = itemView.findViewById(R.id.price);
            ratings = itemView.findViewById(R.id.ratings);
            myRating = itemView.findViewById(R.id.myRatingBarC);
            card = itemView.findViewById(R.id.cardView);



        }
    }
}
