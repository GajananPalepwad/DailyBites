package com.gn4k.dailybites;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class UserFragment extends Fragment {

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        Button logout = view.findViewById(R.id.LogOut);
        TextView name = view.findViewById(R.id.name);
        TextView address = view.findViewById(R.id.address);
        TextView MessName = view.findViewById(R.id.messNameU);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

        name.setText(sharedPreferences.getString("UserName",""));
        address.setText(sharedPreferences.getString("UserAddress",""));
        MessName.setText(sharedPreferences.getString("messName",""));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                SharedPreferences.Editor preferences2 = sharedPreferences.edit();
                preferences2.putString("MessOrUser","");
                preferences2.apply();

                Intent intent = new Intent(getActivity(),LanguageChooser.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        return view;
    }
}