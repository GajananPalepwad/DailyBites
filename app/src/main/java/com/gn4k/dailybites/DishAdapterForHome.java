package com.gn4k.dailybites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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

    ;

    @Override
    public void onBindViewHolder(@NonNull DishAdapterForHome.MyViewHolder holder, int position) {

        MessModel messmodel = list.get(position);
        holder.menu.setText(messmodel.getToDayDish());
        holder.dishprice.setText("₹"+messmodel.getDishPrize());
        holder.ratings.setText(messmodel.getRatings());
        holder.myRating.setRating(Float. parseFloat(messmodel.getRatings()));
        Glide.with(context).load(messmodel.getCoverImage()).centerCrop().placeholder(R.drawable.silver).into(holder.coverImg);
        if(messmodel.getIsVerified().equals("yes")) {
            holder.ver.setText("Verified");
        }else{
            holder.ver.setText("Not Verified");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String latitude = String.valueOf(messmodel.getLatitude());
                String longitude = String.valueOf(messmodel.getLongitude());

                Intent intent = new Intent(context, DishInfo.class);
                intent.putExtra("messMobile", messmodel.getMobileNo());
                intent.putExtra("messName", messmodel.getMessName());
                intent.putExtra("messIsDelivery", messmodel.getIsDelivery());
                intent.putExtra("messCoverImage", messmodel.getCoverImage());
                intent.putExtra("messRatings", messmodel.getRatings());
                intent.putExtra("messDishPrize", messmodel.getDishPrize());
                intent.putExtra("messToDayDish", messmodel.getToDayDish());
                intent.putExtra("messLatitude", latitude);
                intent.putExtra("messLongitude", longitude);

                context.startActivity(intent);

            }

        });


    }


//
//    private void showSettingsBottomSheetDialog(BottomSheetDialog bottomSheetDialog ) {
//
//        // Inflate the layout for the BottomSheetDialog
//        View bottomSheetView = inflaterH.inflate(R.layout.dishlist_bottomsheet, (ConstraintLayout) view.findViewById(R.id.dish_sheet));
//
//        ImageView cover = bottomSheetView.findViewById(R.id.coverImgB);
//        Glide.with(context).load(messmodel.getCoverImage()).centerCrop().placeholder(R.drawable.silver).into(cover);
//
//        TextView menu = bottomSheetView.findViewById(R.id.menuB);
//        menu.setText(messmodel.getToDayDish());
//
//        RatingBar myRatingBar = bottomSheetView.findViewById(R.id.myRatingBarB);
//        myRatingBar.setRating(Float. parseFloat(messmodel.getRatings()));
//
//        TextView priceW = bottomSheetView.findViewById(R.id.Tv_prizeW);
//        if (messmodel.isDelivery.equals("no")){
//            priceW.setText("Not Available");
//        }else {
//            priceW.setText("₹"+(Integer.parseInt(messmodel.getDishPrize())+40)+"");
//        }
//
//        TextView priceWO = bottomSheetView.findViewById(R.id.Tv_prizeWO);
//        priceWO.setText("₹"+messmodel.getDishPrize());
//
//
//        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.show();
//
//
//    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView menu, ver, dishprice, ratings;

        RatingBar myRating;

        ImageView coverImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            menu = itemView.findViewById(R.id.menu);
            coverImg = itemView.findViewById(R.id.coverImg);
            ver = itemView.findViewById(R.id.isVerified);
            dishprice = itemView.findViewById(R.id.price);
            ratings = itemView.findViewById(R.id.ratings);
            myRating = itemView.findViewById(R.id.myRatingBarC);



        }
    }
}
