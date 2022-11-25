package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stavroula.uber.entity.Trip;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverFareActivity extends MainActivity {

    TextView fare_amount_txt;
    Button credit_card_btn,cash_btn;

    String parsedDistance;
    Long tripId;

    double charge = 0.74;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.driver_fare_layout);

        Bundle bundle = getIntent().getExtras();
        tripId = bundle.getLong("tripId");
        parsedDistance = bundle.getString("parsedDistance");

        String[] splited = parsedDistance.split("\\s+");
        final Double distance_value = Double.parseDouble(splited[0]);
        Log.wtf("123", "split"+splited[0]);
        Log.wtf("123", "charge"+charge);
        double total = (distance_value*charge);
        Log.wtf("123", "total!"+total);
        DecimalFormat df = new DecimalFormat("0.00");

        fare_amount_txt = findViewById(R.id.fare_amount_value);
        fare_amount_txt.setText(String.valueOf(df.format(total))+"$");


        credit_card_btn = findViewById(R.id.call_btn);
        credit_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO sendNotification - end trip request
                String creditCard = "creditCard";
                savePayment(creditCard);
                sendPaymentNotification();
                Intent intent = new Intent(DriverFareActivity.this, RatingActivity.class);
                intent.putExtra("tripId", tripId);
                startActivity(intent);

            }
        });

        cash_btn = findViewById(R.id.cancel_btn);
        cash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO notification - end trip request
                String cash = "cash";
                savePayment(cash);
                Intent intent = new Intent(DriverFareActivity.this, RatingActivity.class);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
            }
        });


    }

    private void sendPaymentNotification() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<Long> call = apiService.getPaymentNotification();
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()){
                    Log.wtf("123", "Got notification:Payment");
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });
    }

    private void savePayment(final String type) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<Trip> call = apiService.savePaymentType(type);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful()){
                    Log.wtf("123", "Payment Method"+type);
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {

            }
        });
    }
}
