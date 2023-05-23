package com.gn4k.dailybites;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private BottomNavigationView bottomNavigationView;
    private ImageView mess;
    private int previousScrollY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        mess = view.findViewById(R.id.testcard);


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

        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MessInfo.class);
                startActivity(intent);
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