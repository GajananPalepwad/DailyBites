package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.L;

import java.util.Locale;

public class LanguageChooser extends AppCompatActivity {

    Button eng, hindi, marathi, kannada, telugu, tamil, punjabi, bengali;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_chooser);
        eng = findViewById(R.id.english);
        hindi = findViewById(R.id.hindi);

        eng.setOnClickListener(v -> {

            if(getIntent().getStringExtra("data")!= null) {
                onBackPressed();
            }else if(getIntent().getStringExtra("data") == null){
                Intent intent = new Intent(LanguageChooser.this, ChooseMessOrUser.class);
                startActivity(intent);
            }
        });

        hindi.setOnClickListener(v -> {

            if(getIntent().getStringExtra("data")!= null) {
                setLanguage("hi");
                onBackPressed();
            }else if(getIntent().getStringExtra("data") == null){
                setLanguage("hi");
                Intent intent = new Intent(LanguageChooser.this, ChooseMessOrUser.class);
                startActivity(intent);
            }
        });


    }

    private void setLanguage(String language){

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

    }


}