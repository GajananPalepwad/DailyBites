package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MonthlyPlanEditor extends AppCompatActivity {

    TextView TVplanName;
    ImageView planImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_plan_editor);

        TVplanName = findViewById(R.id.tv_planName);
        planImage = findViewById(R.id.planImage);

        Bundle bundle = getIntent().getExtras();
        String planName = bundle.getString("planName");

        setAllThingsAccordingToPlan(planName);




    }

    private void setAllThingsAccordingToPlan(String planName) {


        TVplanName.setText(planName + " Plan");

        if(planName.equals("Diamond")){

            TVplanName.setTextColor(Color.parseColor("#9747FF"));
            Drawable drawable  = getResources().getDrawable(R.drawable.diamond);
            planImage.setImageDrawable(drawable);


        }
        else if (planName.equals("Gold")) {

            TVplanName.setTextColor(Color.parseColor("#FDD500"));
            Drawable drawable  = getResources().getDrawable(R.drawable.gold);
            planImage.setImageDrawable(drawable);

        }
        else if (planName.equals("Silver")) {

            TVplanName.setTextColor(Color.parseColor("#B5B5B5"));
            Drawable drawable  = getResources().getDrawable(R.drawable.silver);
            planImage.setImageDrawable(drawable);

        }

    }
}