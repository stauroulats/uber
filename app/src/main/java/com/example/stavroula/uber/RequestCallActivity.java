package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stavroula.uber.entity.TripRequest;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestCallActivity extends MainActivity {

    TextView pick_up_point_txt, destination_txt, title_txt, rider_name_txt, rating_txt;

    Button accept_btn, decline_btn;
    String riderName, rating;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.request_call_layout);

        String  body = getIntent().getStringExtra("body");
        String  title = getIntent().getStringExtra("title");
        final Long trip_request_id = Long.valueOf(getIntent().getStringExtra("data1"));
        Long rider_id = Long.valueOf(getIntent().getStringExtra("data2"));

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<TripRequest> call = apiService.getTripRequest(trip_request_id);
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<TripRequest>() {
            @Override
            public void onResponse(Call<TripRequest> call, Response<TripRequest>  response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    int statusCode = response.code();
                    TripRequest tripRequest = response.body();
                    riderName = tripRequest.getRider().getUser().getFirstName() + " " + tripRequest.getRider().getUser().getLastName();
                    rating = tripRequest.getRider().getTotalReviews().toString();

                    pick_up_point_txt = findViewById(R.id.pick_up_point);
                    destination_txt = findViewById(R.id.destination_point);
                    rider_name_txt = findViewById(R.id.rider_name);
                    rating_txt = findViewById(R.id.rating);
                    pick_up_point_txt.setText("Pick Up Point :"+" "+ tripRequest.getPickUpPoint());
                    destination_txt.setText("Destination Point :"+ " " + tripRequest.getDestination());
                    rider_name_txt.setText(tripRequest.getRider().getUser().getFirstName() + " " + tripRequest.getRider().getUser().getLastName()+ " ");
                    rating_txt.setText("Rating: "+tripRequest.getRider().getTotalReviews().toString());
                }
            }

            @Override
            public void onFailure(Call<TripRequest>  call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });



        title_txt = findViewById(R.id.title);
        title_txt.setText(title);


        accept_btn = findViewById(R.id.accept_btn);
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRequest(trip_request_id, riderName,rating);
            //TODO call POST method acceptRequest
            }
        });

        decline_btn = findViewById(R.id.decline_btn);
        decline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO check where back leads else redirect WelcomeDriverActivity
                declineRequest(trip_request_id);
                onBackPressed();
            }
        });
    }

   private void acceptRequest(final Long tripRequestId, final String riderName, final String rating){
       ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

       Call<TripRequest> call = apiService.acceptRequest(tripRequestId);
       Log.d("123", "call" + call.toString());
       call.enqueue(new Callback<TripRequest>() {
           @Override
           public void onResponse(Call<TripRequest> call, Response<TripRequest>  response) {
               Log.wtf("123", "response" + new Gson().toJson(response.body()));

               int msg = response.code();
               Log.d("123", "message" + msg);

               if (response.isSuccessful()) {
                   int statusCode = response.code();
                   //TODO send notification to rider
                   LayoutInflater inflater = LayoutInflater.from(RequestCallActivity.this);
                   View layout_register = inflater.inflate(R.layout.driver_welcome_layout,null);
                   Log.wtf("123", "Got message: " + riderName);
                   Log.wtf("123", "Got message: " + rating);

                   Intent broadcastIntent = new Intent("acceptTripNotification");
                   broadcastIntent.putExtra("tripRequestId",tripRequestId);
                   broadcastIntent.putExtra("riderName",riderName);
                   broadcastIntent.putExtra("rating", rating);
                   LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
                   sendDriverDataNotification(tripRequestId);
                   onBackPressed();
               }
           }

           @Override
           public void onFailure(Call<TripRequest>  call, Throwable t) {
               Log.d("123", "Unable to submit post to API.");
           }
       });
   }

    private void sendDriverDataNotification(Long tripRequestId) {
        final ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.wtf("123", "apiservice" + apiService.toString());

        //TODO id driver
        Call<Long> call = apiService.getDriverDataNotification(Long.valueOf(8), tripRequestId);
        Log.wtf("123", "call" + call.request().toString());
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));
                int msg = response.code();
                Log.wtf("123", "message" + msg);
                if (response.isSuccessful()) {

                    Log.wtf("123", "NOTIFICATION driver data send");
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.wtf("123", "Unable to submit post to API.");
            }
        });

    }
    private void declineRequest(Long tripRequestId){
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<TripRequest> call = apiService.cancelRequest(tripRequestId);
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<TripRequest>() {
            @Override
            public void onResponse(Call<TripRequest> call, Response<TripRequest>  response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {
                    int statusCode = response.code();
                    /*LayoutInflater inflater = LayoutInflater.from(RequestCallActivity.this);
                    View layout_register = inflater.inflate(R.layout.driver_welcome_layout,null);
                    Intent intent = new Intent(RequestCallActivity.this,WelcomeDriverActivity.class);
                    startActivity(intent);*/
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<TripRequest>  call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });
    }
}
