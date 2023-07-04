package com.gn4k.dailybites.Mess.consumersUserlistFragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GoldUserList extends Fragment {


    public GoldUserList() {
        // Required empty public constructor
    }

    LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gold_user_list, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoading();
        // Inflate the layout for this fragment
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MessOwnerData", MODE_PRIVATE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("mess").
                child(sharedPreferences.getString("MessOwnerMobileNo", "")).
                child("Goldplan").child("Users");

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        ArrayList<UserModelForMess> list = new ArrayList<>();
        UserListAdapter adapter = new UserListAdapter(container.getContext(), getActivity(), loadingDialog, list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(model -> {
            // Handle the item click event here
            showDialogBox(model.name, "Email:- "+model.email+"\n"+"Address:- "+model.address+"\n", model.latitude, model.longitude);
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModelForMess user = dataSnapshot.getValue(UserModelForMess.class);
                    list.add(user);
                }
                adapter.notifyDataSetChanged();
                loadingDialog.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        return view;
    }

    private void openGoogleMaps(String latitude, String longitude) {
        String uri = "https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        getActivity().startActivity(intent);
    }


    private void showDialogBox(String title, String mbody, String latitude, String longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(mbody);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.setNegativeButton("Open in Map", (dialog, which) -> {
            openGoogleMaps(latitude, longitude);
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}