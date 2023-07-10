package com.gn4k.dailybites.RoomForWhishList;

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
import com.gn4k.dailybites.User.MessInfo;
import com.gn4k.dailybites.R;

import java.util.List;

// MessAdapter class
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyRecentViewHolder> {


    List<Wishlist> messList;
    Context context;
    ViewGroup parent;
    public WishlistAdapter(Context context, List<Wishlist> messList) {
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
        TextView messName;
        ImageView cover, delete;
        TextView verify;

        MyRecentViewHolder(@NonNull View itemView) {
            super(itemView);
            messName = itemView.findViewById(R.id.messName);
            cover = itemView.findViewById(R.id.coverImg);
            verify = itemView.findViewById(R.id.isVerify);
            itemView.setOnClickListener(this);
            delete = itemView.findViewById(R.id.likeD);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlistcards, parent, false);
        return new MyRecentViewHolder(view);
    }

    String id;
    MyRecentViewHolder holder;

    @Override
    public void onBindViewHolder(@NonNull MyRecentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.holder = holder;
        holder.messName.setText(messList.get(position).getMessNameR());
        Glide.with(parent.getContext()).load(messList.get(position).getUrlCover()).centerCrop().placeholder(R.drawable.silver).into(holder.cover);
        holder.verify.setText(messList.get(position).getIsVerify());
        id = messList.get(position).getMessNoR();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessInfo.class);
                intent.putExtra("messMobile", messList.get(position).getMessNoR());
                intent.putExtra("messName", messList.get(position).getMessNameR());
                intent.putExtra("messLatitude", "");
                intent.putExtra("messLongitude", "");
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Bgthread().start();
                messList.remove(position);
                notifyDataSetChanged();
            }
        });


    }

    class Bgthread extends Thread { // to add a mess in recent list in room database
        public void run() {
            super.run();
            WishlistDatabase db = Room.databaseBuilder(holder.messName.getContext(), WishlistDatabase.class, "WishlistView_DB").allowMainThreadQueries().build();
            WishlistDao messDao = db.userDao();

            if (messDao.isExistByWishlistNo(id)) {
                WishlistDao wishlistDao = db.userDao();
                wishlistDao.delete(wishlistDao.getWishlistByUid(id));
            }

        }
    }

            @Override
    public int getItemCount() {
        return messList.size();
    }

}
