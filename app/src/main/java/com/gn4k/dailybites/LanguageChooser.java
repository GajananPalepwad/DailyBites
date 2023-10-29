package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.L;

import java.util.Locale;

public class LanguageChooser extends AppCompatActivity {

    Button eng, hindi, marathi, kannada, telugu, tamil, punjabi, bengali;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_chooser);
        eng = findViewById(R.id.english);
        hindi = findViewById(R.id.hindi);
        marathi = findViewById(R.id.marathi);
        kannada = findViewById(R.id.kannada);
        telugu = findViewById(R.id.telugu);
        tamil = findViewById(R.id.tamil);
        punjabi = findViewById(R.id.punjabi);
        bengali = findViewById(R.id.bengali);

        sharedPreferences = getSharedPreferences("Settings",MODE_PRIVATE);
        preferences = sharedPreferences.edit();

        eng.setOnClickListener(v -> {

            if(getIntent().getStringExtra("data")!= null) {
                preferences.putString("lang", "");
                preferences.apply();
                setLanguage("");
                onBackPressed();
            }else if(getIntent().getStringExtra("data") == null){
                preferences.putString("lang", "");
                preferences.apply();
                setLanguage("");
                Intent intent = new Intent(LanguageChooser.this, ChooseMessOrUser.class);
                startActivity(intent);
            }
        });

        hindi.setOnClickListener(v -> {

            if(getIntent().getStringExtra("data")!= null) {
                preferences.putString("lang", "hi");
                preferences.apply();
                setLanguage("hi");
                onBackPressed();
            }else if(getIntent().getStringExtra("data") == null){
                preferences.putString("lang", "hi");
                preferences.apply();
                setLanguage("hi");
                Intent intent = new Intent(LanguageChooser.this, ChooseMessOrUser.class);
                startActivity(intent);
            }
        });

        marathi.setOnClickListener(v -> {

            if(getIntent().getStringExtra("data")!= null) {
                preferences.putString("lang", "mr");
                preferences.apply();
                setLanguage("mr");
                onBackPressed();
            }else if(getIntent().getStringExtra("data") == null){
                preferences.putString("lang", "mr");
                preferences.apply();
                setLanguage("mr");
                Intent intent = new Intent(LanguageChooser.this, ChooseMessOrUser.class);
                startActivity(intent);
            }
        });

        kannada.setOnClickListener(v ->{

            Toast.makeText(this, "ಈ ಭಾಷೆಯನ್ನು ಶೀಘ್ರದಲ್ಲೇ ಸೇರಿಸಲಾಗುವುದು", Toast.LENGTH_SHORT).show();
        });

        telugu.setOnClickListener(v ->{

            Toast.makeText(this, "ఈ భాష త్వరలో జోడించబడుతుంది", Toast.LENGTH_SHORT).show();
        });

        tamil.setOnClickListener(v ->{

            Toast.makeText(this, "இந்த மொழி விரைவில் சேர்க்கப்படும்", Toast.LENGTH_SHORT).show();
        });

        punjabi.setOnClickListener(v ->{

            Toast.makeText(this, "ਇਹ ਭਾਸ਼ਾ ਜਲਦੀ ਹੀ ਸ਼ਾਮਲ ਹੋਵੇਗੀ", Toast.LENGTH_SHORT).show();
        });

        bengali.setOnClickListener(v ->{

            Toast.makeText(this, "এই ভাষা শীঘ্রই যোগ হবে", Toast.LENGTH_SHORT).show();
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