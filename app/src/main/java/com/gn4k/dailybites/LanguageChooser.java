package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class LanguageChooser extends AppCompatActivity {

    Button eng, hindi, marathi, kannada, telugu, tamil, punjabi, bengali;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_chooser);

        eng=findViewById(R.id.english);

        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LanguageChooser.this,ChooseMessOrUser.class);
                startActivity(intent);
                finish();
            }
        });


    }
}