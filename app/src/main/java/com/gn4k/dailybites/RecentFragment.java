package com.gn4k.dailybites;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gn4k.dailybites.RoomForRecent.Mess;
import com.gn4k.dailybites.RoomForRecent.MessDao;
import com.gn4k.dailybites.RoomForRecent.MessDatabase;
import com.gn4k.dailybites.RoomForRecent.RecentAdapter;
import com.gn4k.dailybites.RoomForWhishList.Wishlist;
import com.gn4k.dailybites.RoomForWhishList.WishlistAdapter;
import com.gn4k.dailybites.RoomForWhishList.WishlistDao;
import com.gn4k.dailybites.RoomForWhishList.WishlistDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.List;


public class RecentFragment extends Fragment {


    public RecentFragment() {
        // Required empty public constructor
    }

    private RecyclerView recentRecyclerView, wishlistRecycleView;
    private BottomNavigationView bottomNavigationView;
    private int previousScrollY = 0;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                Intent intent = new Intent(getActivity(), NotificationForUser.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        recentRecyclerView = view.findViewById(R.id.recyclerViewRecent);
        wishlistRecycleView = view.findViewById(R.id.recyclerViewWishlist);

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);


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


        setRecentRecyclerView();
        setWishlistRecyclerView();

        return view;
    }

    private void setRecentRecyclerView() {
        new Bgthread(recentRecyclerView).start();
    }
    private void setWishlistRecyclerView() {
        new BgThreadWishlist(wishlistRecycleView).start();
    }


    class Bgthread extends Thread {  // to display recent list in recyclerView
        private RecyclerView recyclerView;

        Bgthread(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void run() {
            super.run();

            MessDatabase messdb = Room.databaseBuilder(getActivity(), MessDatabase.class, "RecentView_DB").build();
            MessDao messDao = messdb.userDao();
            List<Mess> mess = messDao.getAllMess();
            Collections.reverse(mess);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                    RecentAdapter recentAdapter = new RecentAdapter(getActivity(),mess);
                    recyclerView.setAdapter(recentAdapter);
                }
            });
        }
    }



    class BgThreadWishlist extends Thread {  // to display Wishlist list in recyclerView
        private RecyclerView recyclerView;

        BgThreadWishlist(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void run() {
            super.run();

            WishlistDatabase messdb = Room.databaseBuilder(getActivity(), WishlistDatabase.class, "WishlistView_DB").build();
            WishlistDao messDao = messdb.userDao();
            List<Wishlist> mess = messDao.getAllWishlist();
            Collections.reverse(mess);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    WishlistAdapter wishlistAdapter = new WishlistAdapter(getActivity(),mess);
                    recyclerView.setAdapter(wishlistAdapter);
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
