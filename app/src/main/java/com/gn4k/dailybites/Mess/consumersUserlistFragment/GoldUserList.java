package com.gn4k.dailybites.Mess.consumersUserlistFragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gn4k.dailybites.Animatin.LoadingDialog;
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
}