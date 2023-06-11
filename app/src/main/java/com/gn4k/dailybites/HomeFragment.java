package com.gn4k.dailybites;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Toast;

import com.gn4k.dailybites.Animatin.LoadingDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private String EndDate="";
    LoadingDialog loadingDialog;
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
        endSubcription();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingDialog  = new LoadingDialog(getActivity());
        loadingDialog.startLoading();


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
        myAdapter = new MyMessAdapterForHome(container.getContext(), getActivity(), loadingDialog,list);
        recyclerView.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    MessModel user = dataSnapshot.getValue(MessModel.class);
                    list.add(user);
                }
                myAdapter.notifyDataSetChanged();

                loadingDialog.stopLoading();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        if(!ValuesLocal.show) {
            loadingDialog.stopLoading();
            ValuesLocal.show = true;
        }
        super.onResume();

    }

    private void setSubscriptionCard(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

        name.setText(sharedPreferences.getString("UserName",""));
        planName.setText(sharedPreferences.getString("planName",""));
        messName.setText(sharedPreferences.getString("messName",""));
        no.setText(sharedPreferences.getString("MessNo","")+sharedPreferences.getString("UserMobileNo",""));
        endDate.setText(convertDateFormat(sharedPreferences.getString("toDate",""), "MM/dd/yyyy", "dd/MM"));
        EndDate = sharedPreferences.getString("toDate","");

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


    private void endSubcription(){

        GetDateTime getDateTime = new GetDateTime(getActivity());
        getDateTime.getDateTime(new GetDateTime.VolleyCallBack() {
            @Override
            public void onGetDateTime(String date, String time) {
                if (EndDate.equals(date)){

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor preferences = sharedPreferences.edit();

                    preferences.putString("messName","");
                    preferences.putString("MessNo","Buy subscription to get this card");
                    preferences.putString("planName","");
                    preferences.putString("fromDate","");
                    preferences.putString("toDate","");
                    preferences.apply();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("from", FieldValue.delete());
                    updates.put("to", FieldValue.delete());
                    updates.put("planName", FieldValue.delete());
                    updates.put("messNo", FieldValue.delete());
                    updates.put("messName", FieldValue.delete());

                    db.collection("User").
                            document(sharedPreferences.getString("UserEmail", "")).
                            update(updates).
                            addOnSuccessListener(aVoid -> {
                            // Field deleted successfully
                            showInstructionDialogBox("Plan Expired", "Your "+
                                    sharedPreferences.getString("planName","") +
                                    " of " + sharedPreferences.getString("messName","")+ " is EXPIRED TODAY !!!");
                            })
                            .addOnFailureListener(e -> {
                                // Error occurred while deleting the field
                                System.out.println("Error deleting field: " + e.getMessage());
                            });


                }
            }
        });


    }

    private void showInstructionDialogBox(String title, String mbody) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(mbody);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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