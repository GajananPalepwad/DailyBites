package com.gn4k.dailybites.User;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gn4k.dailybites.GetDateTime;
import com.gn4k.dailybites.LanguageChooser;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.SupportOptions;
import com.gn4k.dailybites.User.ProfileForUsers;
import com.gn4k.dailybites.User.SendMessegeToMess;
import com.gn4k.dailybites.User.WalletForUser;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class UserFragment extends Fragment {

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user, container, false);
        Button logout = view.findViewById(R.id.LogOut);
        Button setting = view.findViewById(R.id.Settings);
        Button suppurt = view.findViewById(R.id.support);
        Button persnolInfo = view.findViewById(R.id.persnolI);
        TextView name = view.findViewById(R.id.name);
        TextView address = view.findViewById(R.id.address);
        TextView MessName = view.findViewById(R.id.messNameU);
        TextView daysRemain = view.findViewById(R.id.daysRemain);
        TextView walletBalance = view.findViewById(R.id.tvWalletBalance);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

        name.setText(sharedPreferences.getString("UserName",""));
        address.setText(sharedPreferences.getString("UserAddress",""));
        if(!sharedPreferences.getString("messName","").equals("")) {
            MessName.setText(sharedPreferences.getString("messName", ""));
        }
        walletBalance.setText(getString(R.string.rupee)+" "+sharedPreferences.getString("previousBalance", "")+ "\nWallet");



            GetDateTime getDateTime = new GetDateTime(getActivity());
            getDateTime.getDateTime((date, time) -> {

                String givenDateStr = sharedPreferences.getString("toDate", "");
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                // Parse the current date string to a LocalDate object
                Date currentDate = null;
                Date givenDate = null;
                try {
                    currentDate = formatter.parse(date);
                    givenDate = formatter.parse(givenDateStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Calculate the number of days between the given date and the current date
                long remainingDays = -1;
                if (currentDate != null && givenDate != null) {
                    long diffInMilliSec = givenDate.getTime() - currentDate.getTime();
                    remainingDays = TimeUnit.DAYS.convert(diffInMilliSec, TimeUnit.MILLISECONDS);
                }
                if(remainingDays==-1) {
                    daysRemain.setText("0 Days Left");
                }else {
                    daysRemain.setText(remainingDays + " Days Left");
                }

            });

        ImageView wallet = view.findViewById(R.id.wallet);

        ImageView notification = view.findViewById(R.id.notification);
        wallet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WalletForUser.class);
            startActivity(intent);
        });

        notification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SendMessegeToMess.class);
            startActivity(intent);
        });


        persnolInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileForUsers.class);
            startActivity(intent);
        });

        suppurt.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SupportOptions.class);
            startActivity(intent);
        });

        setting.setOnClickListener(v -> showSettingsBottomSheetDialog());

        logout.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    // Method to show the logout dialog
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Call the logout function
            Logout();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }






    private BottomSheetDialog bottomSheetDialog;
    private void showSettingsBottomSheetDialog() {


        // Inflate the layout for the BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.setting_bottomsheet, (ConstraintLayout) view.findViewById(R.id.setting_sheet));

        // Find your button or any other view inside the BottomSheetDialog layout
        CardView notifi = bottomSheetView.findViewById(R.id.notificationSettings);
        CardView cache = bottomSheetView.findViewById(R.id.clearCache);
        CardView lan = bottomSheetView.findViewById(R.id.lan);

        ImageView imgN = bottomSheetView.findViewById(R.id.imgN);
        ImageView imgC = bottomSheetView.findViewById(R.id.imgC);
        ImageView imgL = bottomSheetView.findViewById(R.id.imgL);


        TextView textN = bottomSheetView.findViewById(R.id.textN);
        TextView textC = bottomSheetView.findViewById(R.id.textC);
        TextView textL = bottomSheetView.findViewById(R.id.textL);


        ImageView nextN = bottomSheetView.findViewById(R.id.nextN);
        ImageView nextC = bottomSheetView.findViewById(R.id.nextC);
        ImageView nextL = bottomSheetView.findViewById(R.id.nextL);
        // Set click listener for the button inside the BottomSheetDialog

        notifi.setOnClickListener(this::notifi);
        imgN.setOnClickListener(this::notifi);
        textN.setOnClickListener(this::notifi);
        nextN.setOnClickListener(this::notifi);


        cache.setOnClickListener(this::cashe);
        imgC.setOnClickListener(this::cashe);
        textC.setOnClickListener(this::cashe);
        nextC.setOnClickListener(this::cashe);


        lan.setOnClickListener(this::lan);
        imgL.setOnClickListener(this::lan);
        textL.setOnClickListener(this::lan);
        nextL.setOnClickListener(this::lan);


        // Create the BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    public void cashe (View v){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
        startActivity(intent);
    }


    public void notifi (View v){
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
        startActivity(intent);
    }


    public void lan (View v){
        Intent intent = new Intent(getActivity(), LanguageChooser.class);
        intent.putExtra("data", "settings");
        startActivity(intent);
    }

    private void Logout(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor preferences = sharedPreferences.edit();

        preferences.putString("UserEmail","");
        preferences.putString("UserPassword","");
        preferences.putString("UserMobileNo","");
        preferences.putString("UserName","");
        preferences.putString("UserLatitude","");
        preferences.putString("UserLongitude","");
        preferences.putString("UserAddress","");
        preferences.putString("messName","");
        preferences.putString("MessNo","");
        preferences.putString("planName","");
        preferences.putString("fromDate","");
        preferences.putString("toDate","");
        preferences.apply();

        SharedPreferences sharedPreferencesChoose = getActivity().getSharedPreferences("Choose", MODE_PRIVATE);
        SharedPreferences.Editor preferences2 = sharedPreferencesChoose.edit();
        preferences2.putString("MessOrUser","");
        preferences2.apply();

        Intent intent = new Intent(getActivity(),LanguageChooser.class);
        startActivity(intent);
        getActivity().finish();
    }



}