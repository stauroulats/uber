package com.example.stavroula.uber;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stavroula.uber.adapter.TripAdapter;
import com.example.stavroula.uber.entity.Trip;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripHistoryActivity extends MainActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ImageButton return_back_btn;
    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rtrip_history_layout);

        mResponse = findViewById(R.id.mresponse);

        //Return previous activity button
        return_back_btn =  findViewById(R.id.return_button);
        return_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice" + apiService.toString());

        Call<List<Trip>> call = apiService.getTrips();
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    int statusCode = response.code();
                    List<Trip> trips= response.body();
                    adapter = new TripAdapter(getApplicationContext(), trips);
                    recyclerView.setAdapter(adapter);


                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response" + response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
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
