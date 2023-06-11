package com.gn4k.dailybites.RoomForCalender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.MessInfo;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.RoomForWhishList.Wishlist;
import com.gn4k.dailybites.RoomForWhishList.WishlistDao;
import com.gn4k.dailybites.RoomForWhishList.WishlistDatabase;

import java.util.List;

// MessAdapter class
public class CalendereAdapter extends RecyclerView.Adapter<CalendereAdapter.MyRecentViewHolder> {


    List<Calender> messList;
    Context context;
    ViewGroup parent;
    public CalendereAdapter(Context context, List<Calender> messList) {
        this.context = context;
        this.messList = messList;
    }

    private static OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    static class MyRecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView menu, date, month, day;

        MyRecentViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.food_description);
            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            day = itemView.findViewById(R.id.weekday);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }

            }
        }
    }



    @NonNull
    @Override
    public MyRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent; // Set the parent variable to the provided parent
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calender_card, parent, false);
        return new MyRecentViewHolder(view);
    }

    String id;
    MyRecentViewHolder holder;

    @Override
    public void onBindViewHolder(@NonNull MyRecentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.holder = holder;

        String date = messList.get(position).getDate();
        if (date != null) {
            // Split the date string using the delimiter "/"
            String[] parts = date.split("/");

            // Extract the month and day from the split parts
            if (parts.length >= 2) {
                String months = parts[0];
                String dates = parts[1];
                String monthAbbreviation = "";

                switch (months) {
                    case "01":
                        monthAbbreviation = "JAN";
                        break;
                    case "02":
                        monthAbbreviation = "FEB";
                        break;
                    case "03":
                        monthAbbreviation = "MAR";
                        break;
                    case "04":
                        monthAbbreviation = "APR";
                        break;
                    case "05":
                        monthAbbreviation = "MAY";
                        break;
                    case "06":
                        monthAbbreviation = "JUN";
                        break;
                    case "07":
                        monthAbbreviation = "JUL";
                        break;
                    case "08":
                        monthAbbreviation = "AUG";
                        break;
                    case "09":
                        monthAbbreviation = "SEP";
                        break;
                    case "10":
                        monthAbbreviation = "OCT";
                        break;
                    case "11":
                        monthAbbreviation = "NOV";
                        break;
                    case "12":
                        monthAbbreviation = "DEC";
                        break;
                    default:
                        monthAbbreviation = "Invalid month";
                        break;
                }

                holder.day.setText(messList.get(position).getMessNoR() + "");
                holder.date.setText(dates);
                holder.menu.setText(messList.get(position).getMenu() + "");
                holder.month.setText(monthAbbreviation + "");
            }
        }

    }

            @Override
    public int getItemCount() {
        return messList.size();
    }

}
