package com.gn4k.dailybites;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.Animation.RatingsDialog;
import com.gn4k.dailybites.User.OfferCodeScanner;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    private BottomNavigationView bottomNavigationView;
    private int previousScrollY = 0;
    RecyclerView recyclerView, recyclerViewforDish;
    DatabaseReference database;
    MyMessAdapterForHome myAdapter;
    DishAdapterForHome dishAdapter;
    ArrayList<MessModel> list, listDish;
    private TextView messName, planName, name, no, endDate;
    private ImageView planImg;
    ConstraintLayout offerCard, messCard;
    LoadingDialog loadingDialog;
    SharedPreferences sharedPreferences;
    private static final int MY_REQUEST_CODE = 100;
    View view;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messName = getActivity().findViewById(R.id.messNameH);
        planName = getActivity().findViewById(R.id.planName);
        name = getActivity().findViewById(R.id.nameH);
        no = getActivity().findViewById(R.id.NoSerial);
        endDate = getActivity().findViewById(R.id.endDate);
        planImg = getActivity().findViewById(R.id.planImg);

        ImageView wallet = view.findViewById(R.id.wallet);
        ImageView notification = view.findViewById(R.id.notification);

        offerCard = view.findViewById(R.id.offer_layout);
        offerCard.setOnClickListener(v -> openScanner(v));

        messCard = view.findViewById(R.id.messCardLayout);

        sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor preferences = sharedPreferences.edit();

        messCard.setOnClickListener(v->{
            if(sharedPreferences.getString("isRating", "").equals("1")){

                RatingsDialog ratingsDialog = new RatingsDialog(getActivity());
                ratingsDialog.showDialog(sharedPreferences.getString("MessNo", ""));
                preferences.putString("isRating", "0");
                preferences.apply();

            }
        });

        if(!sharedPreferences.getString("planName","").equals("")
                && !sharedPreferences.getString("freeDish","").equals("0")){
            offerCard.setVisibility(View.VISIBLE);
        } else if(sharedPreferences.getString("planName","").equals("")
                || sharedPreferences.getString("freeDish","").equals("0")){
            offerCard.setVisibility(View.GONE);
        }



        wallet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WalletForUser.class);
            startActivity(intent);
        });

        notification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SendMessegeToMess.class);
            startActivity(intent);
        });



        setSubscriptionCard();
        checkForAppUpdate();
        endSubcription();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!sharedPreferences.getString("planName","").equals("")
                && !sharedPreferences.getString("freeDish","").equals("0")){
            offerCard.setVisibility(View.VISIBLE);
        } else if(sharedPreferences.getString("planName","").equals("")
                || sharedPreferences.getString("freeDish","").equals("0")){
            offerCard.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingDialog  = new LoadingDialog(getActivity());
        loadingDialog.startLoading();


        view = inflater.inflate(R.layout.fragment_home, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);


        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY+1 > previousScrollY) {
                // Scrolling down
                hideNavigationBar(bottomNavigationView);

            } else if (scrollY-1 < previousScrollY) {
                // Scrolling up
                showNavigationBar(bottomNavigationView);
            }
            previousScrollY = scrollY;
        });

        recyclerView = view.findViewById(R.id.recyclerViewMess);
        recyclerViewforDish = view.findViewById(R.id.recyclerViewDish);

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

                for (MessModel mess : list) {
                    double distance = calculateDistance(
                            Double.parseDouble(sharedPreferences.getString("UserLatitude","")),
                            Double.parseDouble(sharedPreferences.getString("UserLongitude","")),
                            mess.getLatitude(), mess.getLongitude());
                    mess.setDistance(distance);
                }
                Collections.sort(list, Comparator.comparingDouble(MessModel::getDistance));

                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        recyclerViewforDish.setHasFixedSize(true);

        recyclerViewforDish.setLayoutManager(new LinearLayoutManager(container.getContext()));

        listDish = new ArrayList<>();
        dishAdapter = new DishAdapterForHome(container.getContext(), getActivity(), loadingDialog,list,view,getLayoutInflater());
        recyclerViewforDish.setAdapter(dishAdapter);

        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    MessModel user = dataSnapshot.getValue(MessModel.class);

                    listDish.add(user);

                }

                for (MessModel mess : listDish) {
                    double distance = calculateDistance(
                            Double.parseDouble(sharedPreferences.getString("UserLatitude","")),
                            Double.parseDouble(sharedPreferences.getString("UserLongitude","")),
                            mess.getLatitude(), mess.getLongitude());
                    mess.setDistance(distance);
                }
                Collections.sort(listDish, Comparator.comparingDouble(MessModel::getDistance));

                dishAdapter.notifyDataSetChanged();

                loadingDialog.stopLoading();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

//        deleteSub();

        return view;
    }




    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) { //calculate Distance from user to mess
        double R = 6371; // Radius of the earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in kilometers
        return distance;
    }

    @Override
    public void onPause() {

        super.onPause();
        loadingDialog.stopLoading();
    }


    private void setSubscriptionCard(){


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

        if(!sharedPreferences.getString("messName","").equals("")) {
            name.setText(sharedPreferences.getString("UserName", ""));
            planName.setText(sharedPreferences.getString("planName", ""));
            messName.setText(sharedPreferences.getString("messName", ""));
            no.setText(sharedPreferences.getString("MessNo", "") + sharedPreferences.getString("UserMobileNo", ""));
            endDate.setText(convertDateFormat(sharedPreferences.getString("toDate", ""), "MM/dd/yyyy", "dd/MM"));


            switch (sharedPreferences.getString("planName", "")) {
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

    private void deleteSub(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor preferences = sharedPreferences.edit();

        String plan = "";
        if(sharedPreferences.getString("planName", "").equals("Gold Plan")){
            plan ="Goldplan";
        } else if (sharedPreferences.getString("planName", "").equals("Silver Plan")) {
            plan ="Silverplan";
        } else if (sharedPreferences.getString("planName", "").equals("Diamond Plan")) {
            plan ="Diamondplan";
        }

        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = ref.getReference("mess").
                child(sharedPreferences.getString("MessNo", "")).
                child(plan).
                child("Users").
                child(sharedPreferences.getString("UserMobileNo", ""));
        dataRef.removeValue();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> updates = new HashMap<>();
        updates.put("from", "");
        updates.put("to", "");
        updates.put("planName", "");
        updates.put("messNo", "");
        updates.put("messName", "");

        db.collection("User").
                document(sharedPreferences.getString("UserEmail", "")).
                update(updates).
                addOnSuccessListener(aVoid -> {
                    // Field deleted successfully
                    showInstructionDialogBox("Plan Expired", "Your plan is Expired TODAY !!!");
                })
                .addOnFailureListener(e -> {
                    // Error occurred while deleting the field
                });


        preferences.putString("messName", "");
        preferences.putString("MessNo", "");
        preferences.putString("planName", "");
        preferences.putString("fromDate", "");
        preferences.putString("toDate", "");
        preferences.apply();

    }



    private void endSubcription(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        if(!sharedPreferences.getString("planName", "").equals("")) {
            GetDateTime getDateTime = new GetDateTime(getActivity());
            getDateTime.getDateTime((date, time) -> {

                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        Date currentDate = format.parse(date);
                        Date dueDate = format.parse(sharedPreferences.getString("toDate", ""));

                        if (sharedPreferences.getString("toDate", "").equals(date) || currentDate.compareTo(dueDate)>0) {

                            SharedPreferences.Editor preferences = sharedPreferences.edit();


                            //remove data from user side
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("from", "");
                            updates.put("to", "");
                            updates.put("planName", "");
                            updates.put("messNo", "");
                            updates.put("messName", "");
                            updates.put("messToken", "");

                            db.collection("User").
                                    document(sharedPreferences.getString("UserEmail", "")).
                                    update(updates).
                                    addOnSuccessListener(aVoid -> {
                                        // Field deleted successfully
                                        showInstructionDialogBox("Plan Expired", "Your " +
                                                sharedPreferences.getString("planName", "") +
                                                " of " + sharedPreferences.getString("messName", "") + " is EXPIRED TODAY !!!");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error occurred while deleting the field
                                        System.out.println("Error deleting field: " + e.getMessage());
                                    });

                            //remove your data from mess side
                            FirebaseDatabase ref = FirebaseDatabase.getInstance();
                            DatabaseReference dataRef = ref.getReference("mess").
                                    child(sharedPreferences.getString("MessNo", "")).
                                    child("OneDayPlan").
                                    child("Users").
                                    child(sharedPreferences.getString("UserMobileNo", ""));
                            dataRef.removeValue();


                            //remove your data from sharedPreferences
                            preferences.putString("messName", "");
                            preferences.putString("MessNo", "");
                            preferences.putString("planName", "");
                            preferences.putString("fromDate", "");
                            preferences.putString("toDate", "");
                            preferences.putString("messToken", "");
                            preferences.apply();

                        }
                    }catch (Exception ignored){}

            });
        }
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


    private void checkForAppUpdate(){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getContext());

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // an activity result launcher registered via registerForActivityResult
                            AppUpdateType.IMMEDIATE,
                            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                            getActivity(),
                            // flexible updates.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }else{
                Toast.makeText(getActivity(), "NOT", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle callback
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(getContext(), requestCode + "\n" + resultCode, Toast.LENGTH_SHORT).show();
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
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


    public void openScanner(View view) {
            Intent intent = new Intent(getActivity(), OfferCodeScanner.class);
            startActivity(intent);
    }
}