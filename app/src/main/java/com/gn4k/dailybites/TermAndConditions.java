package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;

public class TermAndConditions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_and_conditions);
        CardView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());
    }
}