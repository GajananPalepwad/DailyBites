package com.gn4k.dailybites;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private BottomNavigationView bottomNavigationView;

    private int previousScrollY = 0;
    RecyclerView recyclerView;
    DatabaseReference database;
    MyMessAdapterForHome myAdapter;
    ArrayList<MessModel> list;
    private TextView messName, planName, name, no, endDate;
    private ImageView planImg;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messName = getActivity().findViewById(R.id.messNameH);
        planName = getActivity().findViewById(R.id.planName);
        name = getActivity().findViewById(R.id.nameH);
        no = getActivity().findViewById(R.id.NoSerial);
        endDate = getActivity().findViewById(R.id.endDate);
        planImg = getActivity().findViewById(R.id.planImg);

        setSubscriptionCard();
    }

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

    private void setSubscriptionCard(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

        name.setText(sharedPreferences.getString("UserName",""));
        planName.setText(sharedPreferences.getString("planName",""));
        messName.setText(sharedPreferences.getString("messName",""));
        no.setText(sharedPreferences.getString("UserMobileNo","")+sharedPreferences.getString("MessNo",""));
        endDate.setText(convertDateFormat(sharedPreferences.getString("toDate",""), "MM/dd/yyyy", "dd/MM"));

        switch (sharedPreferences.getString("planName","")){
            case "Diamond Plan":
                planImg.setImageResource(R.drawable.diamond);
                break;
            case "Gold Plan":
                planImg.setImageResource(R.drawable.gold);
                break;
            case "Silver Plan":
                planImg.setImageResource(R.drawable.silver);
                break;
            default:
                planImg.setImageResource(R.drawable.bearfast);
        }

    }

    public static String convertDateFormat(String inputDate, String inputFormat, String outputFormat) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
        try {
            Date date = inputDateFormat.parse(inputDate);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
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