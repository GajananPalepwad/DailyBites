package com.gn4k.dailybites;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.Animation.RatingsDialog;
import com.gn4k.dailybites.RoomForCalender.Calender;
import com.gn4k.dailybites.RoomForCalender.CalenderDao;
import com.gn4k.dailybites.RoomForCalender.CalenderDatabase;
import com.gn4k.dailybites.RoomForCalender.CalendereAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.List;

public class CalenderFragment extends Fragment {

    public CalenderFragment() {
        // Required empty public constructor
    }

    private RecyclerView recentRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private int previousScrollY = 0;
    TextView text;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        recentRecyclerView = view.findViewById(R.id.recyclerViewCalender);
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoading();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

        if(sharedPreferences.getString("isRating", "").equals("1")){

            RatingsDialog ratingsDialog = new RatingsDialog(getActivity());
            ratingsDialog.showDialog(sharedPreferences.getString("MessNo", ""), sharedPreferences);

        }

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        text = view.findViewById(R.id.text);
        ImageView wallet = view.findViewById(R.id.wallet);

        ImageView notification = view.findViewById(R.id.notification);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WalletForUser.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMessegeToMess.class);
                startActivity(intent);
            }
        });



        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY+1 > previousScrollY) {
                    // Scrolling down
                    hideNavigationBar(bottomNavigationView);

                } else if (scrollY-1 < previousScrollY) {
                    // Scrolling up
                    showNavigationBar(bottomNavigationView);
                }
                previousScrollY = scrollY;
            }
        });


        new Bgthread(recentRecyclerView, loadingDialog).start();
        return view;
    }


    class Bgthread extends Thread {  // to display recent list in recyclerView
        private RecyclerView recyclerView;
        private LoadingDialog loadingDialog;

        Bgthread(RecyclerView recyclerView, LoadingDialog loadingDialog) {
            this.recyclerView = recyclerView;
            this.loadingDialog =loadingDialog;
        }

        public void run() {
            super.run();

            CalenderDatabase messdb = Room.databaseBuilder(getActivity(), CalenderDatabase.class, "CalenderView_DB").build();
            CalenderDao messDao = messdb.userDao();
            List<Calender> mess = messDao.getAllCalender();
            Collections.reverse(mess);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    CalendereAdapter recentAdapter = new CalendereAdapter(getActivity(),mess);
                    recyclerView.setAdapter(recentAdapter);
                    if(recentAdapter.getItemCount()!=0){
                        text.setVisibility(View.GONE);
                    }
                    loadingDialog.stopLoading();
                }
            });

        }
    }




    private void hideNavigationBar(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", view.getHeight()+(view.getHeight()/2));
        animator.setDuration(50);
        animator.start();
    }

    // Method to show the bottom navigation bar with animation
    private void showNavigationBar(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(50);
        animator.start();
    }



}