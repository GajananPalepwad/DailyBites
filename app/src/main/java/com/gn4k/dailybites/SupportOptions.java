package com.gn4k.dailybites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gn4k.dailybites.User.MessInfo;
import com.gn4k.dailybites.User.PlanInfo;

public class SupportOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_options);

        Button chatUsButton = findViewById(R.id.chatUs);
        Button termAndConditionsButton = findViewById(R.id.termAndConditions);
        Button openSourceLibrariesButton = findViewById(R.id.OpenSourceLibraries);
        Button registrationsButton = findViewById(R.id.registrations);
        Button feedback = findViewById(R.id.sendFeedback);
        Button aboutUs = findViewById(R.id.aboutUs);


        CardView back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        chatUsButton.setOnClickListener(v -> {

            String phoneNumber = "9421535009";
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + "Hi");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        termAndConditionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(SupportOptions.this, TermAndConditions.class);
            startActivity(intent);
        });

        openSourceLibrariesButton.setOnClickListener(v -> {
            Intent intent = new Intent(SupportOptions.this, OpenSourceLibraries.class);
            startActivity(intent);
        });

        registrationsButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Udyam Registration Number");
                builder.setMessage("UDYAM-MH-21-0044447");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
        });


        feedback.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://forms.gle/nxkLHBXU4LVHsW1v6");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });


        aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(SupportOptions.this, AboutUs.class);
            startActivity(intent);
        });


    }
}