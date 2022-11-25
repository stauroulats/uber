package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stavroula.uber.adapter.CarAdapter;
import com.example.stavroula.uber.entity.Car;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehiclesActivity extends MainActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    private final String TAG = "DEBUG_TAG";


    private ArrayList<Car> cars = new ArrayList<>();
    private CarAdapter carAdapter;

    ImageButton return_back_btn;
    Button add_car_btn;
    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_vehicle_list);

        mResponse = findViewById(R.id.mresponse);

        //Return previous activity button
        return_back_btn = findViewById(R.id.return_button);
        return_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        Log.d("123", "RECYCLEVIEW" + recyclerView.toString());

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        /*cars = new ArrayList<>();
        adapter = new CarAdapter(this,cars);
        recyclerView.setAdapter(adapter);*/

        parseJson();
    }

        private void parseJson () {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice" + apiService.toString());

        Call<List<Car>> call = apiService.getCars();
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                Log.wtf("test", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("test", "message" + msg);

                if (response.isSuccessful()) {
                    int statusCode = response.code();

                    cars = new ArrayList<>(response.body());

                    carAdapter = new CarAdapter(cars, VehiclesActivity.this);
                    recyclerView.setAdapter(carAdapter);

                 /*  adapter = new CarAdapter(getApplicationContext(), cars);
                   recyclerView.setAdapter(adapter); */

                 /*   Log.d("123", "RECYCLEVIEW" + recyclerView.toString());
                    Log.d("123", "ADAPTER" + adapter.toString()); */

                    /* recyclerView.setAdapter(adapter);*/

                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response" + response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });


        Toast.makeText(VehiclesActivity.this, "These are all your Credit Cards",
                Toast.LENGTH_SHORT).show();


        add_car_btn = findViewById(R.id.add_car_btn);
        add_car_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehiclesActivity.this, CreateCarActivity.class);
                startActivity(intent);
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

