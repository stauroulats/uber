package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RiderFareActivity extends MainActivity {

    TextView fare_amount_txt;
    Button ok_btn;

    String parsedDistance;
    Long tripId;

    double charge = 0.74;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rider_fare_layout);

        Bundle bundle = getIntent().getExtras();
        tripId = bundle.getLong("tripId");
        parsedDistance = bundle.getString("parsedDistance");

        String[] splited = parsedDistance.split("\\s+");
        final Double distance_value = Double.parseDouble(splited[0]);
        Log.wtf("123", "split" + splited[0]);
        Log.wtf("123", "charge" + charge);
        double total = (distance_value * charge);
        Log.wtf("123", "total!" + total);
        DecimalFormat df = new DecimalFormat("0.00");

        fare_amount_txt = findViewById(R.id.fare_amount_value);
        fare_amount_txt.setText(String.valueOf(df.format(total)) + "$");

        ok_btn = findViewById(R.id.call_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO sendNotification - end trip request
                Intent intent = new Intent(RiderFareActivity.this, RatingActivity.class);
                intent.putExtra("tripId", tripId);
                startActivity(intent);

            }
        });

    }

}
