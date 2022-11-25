package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.stavroula.uber.entity.Rating;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingActivity extends MainActivity {

    Long tripId;

    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_layout);

         mResponse =  findViewById(R.id.mresponse);

        Bundle bundle = getIntent().getExtras();
        tripId = bundle.getLong("tripId");

        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        ImageButton return_btn = findViewById(R.id.return_button);
        ImageButton close_btn = findViewById(R.id.close_button);
        final EditText feedback = findViewById(R.id.rating_feedback);
        Button submit_btn = findViewById(R.id.submit);


        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer stars = ratingBar.getNumStars();
                String feedback_txt = String.valueOf(feedback.getText());

                Rating rating = new Rating();
                rating.setStars(stars);
                rating.setDescription(feedback_txt);

                saveRating(rating);
                Intent intent = new Intent(RatingActivity.this, WelcomeDriverActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveRating(Rating rating){

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Rating> call = apiService.saveRating(tripId,rating);
        call.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {

                if (response.isSuccessful()) {
                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response"+response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });
    }

    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }
}
