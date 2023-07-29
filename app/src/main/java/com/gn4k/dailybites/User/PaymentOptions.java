package com.gn4k.dailybites.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.gn4k.dailybites.R;

public class PaymentOptions extends AppCompatActivity {

    String price;
    TextView tvplanPrice;
    Button btnPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        btnPay = findViewById(R.id.btnPay);
        tvplanPrice = findViewById(R.id.tvPlanPrice);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        price = extras.getString("price");
        setAllValues();
    }

    private void setAllValues(){
        tvplanPrice.setText(getString(R.string.rupee)+price);
        btnPay.setText("Pay "+getString(R.string.rupee)+price);
    }
}