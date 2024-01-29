package com.shashanksp.smartsonics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.shashanksp.smartsonics.R;

public class PaymentGatewayActivity extends AppCompatActivity {
    Button payBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);
        payBtn = findViewById(R.id.payBtn);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaymentGatewayActivity.this,ListenActivity.class);
                startActivity(i);
            }
        });
    }
}