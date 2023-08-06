package com.gn4k.dailybites.Mess.OneDayPlanClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.Mess.consumersUserlistFragment.UserListAdapter;
import com.gn4k.dailybites.Mess.consumersUserlistFragment.UserModelForMess;
import com.gn4k.dailybites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OneDayUserList extends AppCompatActivity {

    LoadingDialog loadingDialog;
    LinearLayout text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_day_user_list);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoading();
        text = findViewById(R.id.text);

        CardView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());

        SharedPreferences sharedPreferences = this.getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("mess").
                child(sharedPreferences.getString("MessOwnerMobileNo", "")).
                child("OneDayPlan").child("Users");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<OneDayUserModel> list = new ArrayList<>();
        OneDayUserAdapter adapter = new OneDayUserAdapter(this, list );
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(model -> {
            // Handle the item click event here
            Toast.makeText(OneDayUserList.this, "Clicked item: " + model.name, Toast.LENGTH_SHORT).show();
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OneDayUserModel user = dataSnapshot.getValue(OneDayUserModel.class);
                    list.add(user);
                }
                adapter.notifyDataSetChanged();
                loadingDialog.stopLoading();
                if(adapter.getItemCount()!=0){
                    text.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });




    }
}