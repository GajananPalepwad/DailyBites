package com.gn4k.dailybites.RoomForRecent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gn4k.dailybites.R;

import java.util.List;

// MessAdapter class
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {
    private List<Mess> messList;

    public void setData(List<Mess> messList) {
        this.messList = messList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messlistcards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mess mess = messList.get(position);
        holder.bind(mess);
    }

    @Override
    public int getItemCount() {
        return messList != null ? messList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messNameTextView;
        private TextView messNoTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            messNameTextView = itemView.findViewById(R.id.messNameTextView);
//            messNoTextView = itemView.findViewById(R.id.messNoTextView);
        }

        public void bind(Mess mess) {
//            messNameTextView.setText(mess.getMessNameR());
//            messNoTextView.setText(mess.getMessNoR());
        }
    }
}
