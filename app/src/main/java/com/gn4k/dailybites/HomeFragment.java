package com.gn4k.dailybites;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    RecyclerView recyclerView;
    DatabaseReference database;
    MyMessAdapterForHome myAdapter;
    ArrayList<MessModel> list;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    private BottomNavigationView bottomNavigationView;

    private int previousScrollY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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

        recyclerView = view.findViewById(R.id.recyclerViewMess);
        database = FirebaseDatabase.getInstance().getReference("mess");
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.HORIZONTAL, false));

        list = new ArrayList<>();
        myAdapter = new MyMessAdapterForHome(container.getContext(),list);
        recyclerView.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    MessModel user = dataSnapshot.getValue(MessModel.class);
                    list.add(user);
                }
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }


    // Method to hide the bottom navigation bar with animation
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