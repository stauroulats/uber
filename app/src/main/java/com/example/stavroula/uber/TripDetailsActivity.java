package com.example.stavroula.uber;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stavroula.uber.entity.Trip;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDetailsActivity extends MainActivity {

    TextView data_txt,pick_up_point_txt, destnation_txt, ride_distance_txt, time_taken_txt, wait_time_txt , fare_amount_txt ,mResponse;
    ImageButton return_btn;
    Button save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_details_layout);

        mResponse = findViewById(R.id.mresponse);

        data_txt = findViewById(R.id.date);
        pick_up_point_txt = findViewById(R.id.pick_up_point);
        destnation_txt = findViewById(R.id.destination_point);
        ride_distance_txt = findViewById(R.id.ride_distance_value);
        time_taken_txt = findViewById(R.id.time_taken_value);
        wait_time_txt = findViewById(R.id.wait_time_value);
        fare_amount_txt = findViewById(R.id.fare_amount_value);

        return_btn = findViewById(R.id.return_button);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRider();
            }
        });


        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice" + apiService.toString());

        Call<Trip> call = apiService.getTrip();
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    int statusCode = response.code();
                    Trip trip= response.body();


                    data_txt.setText(trip.getDate());
                    pick_up_point_txt.setText(trip.getTripRequest().getPickUpPoint());
                    destnation_txt.setText(trip.getTripRequest().getDestination());
                    ride_distance_txt.setText(String.valueOf(trip.getRideDistance())+"km");
                    fare_amount_txt.setText(String.valueOf(trip.getFare())+"€");

                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response" + response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

    }

    private void updateRider(){

    }

    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }

}
