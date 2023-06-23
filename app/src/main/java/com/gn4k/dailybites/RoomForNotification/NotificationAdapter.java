package com.gn4k.dailybites.RoomForNotification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gn4k.dailybites.R;

import java.util.List;

// MessAdapter class
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyNotificationViewHolder> {


    List<NotificationData> notificationDataList;
    Context context;
    ViewGroup parent;
    public NotificationAdapter(Context context, List<NotificationData> notificationDataList) {
        this.context = context;
        this.notificationDataList = notificationDataList;
    }

    private static OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    static class MyNotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, body, time;

        MyNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            time = itemView.findViewById(R.id.dateTime);
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
    public MyNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent; // Set the parent variable to the provided parent
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new MyNotificationViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyNotificationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(notificationDataList.get(position).getTitle());
        holder.body.setText(notificationDataList.get(position).getBody());
        if(notificationDataList.get(position).getTime()!=null) {
            holder.time.setText(notificationDataList.get(position).getTime() + "");
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, MessInfo.class);
//                intent.putExtra("messMobile", messList.get(position).getBody());
//                intent.putExtra("messName", messList.get(position).getTitle());
//                intent.putExtra("messLatitude", "");
//                intent.putExtra("messLongitude", "");
//                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return notificationDataList.size();
    }

}
