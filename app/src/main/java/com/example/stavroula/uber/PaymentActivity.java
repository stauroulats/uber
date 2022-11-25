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

import com.example.stavroula.uber.adapter.CreditCardAdapter;
import com.example.stavroula.uber.entity.CreditCard;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends MainActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ImageButton return_back_btn;
    Button add_card_btn;
    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_cards_recycler_view);

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

        Call<List<CreditCard>> call = apiService.getCreditCards();
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<List<CreditCard>>() {
            @Override
            public void onResponse(Call<List<CreditCard>> call, Response<List<CreditCard>> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    int statusCode = response.code();
                    List<CreditCard> creditCards = response.body();
                    adapter = new CreditCardAdapter(getApplicationContext(), creditCards);
                    recyclerView.setAdapter(adapter);


                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response" + response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<CreditCard>> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

        Toast.makeText(PaymentActivity.this, "These are all your Credit Cards",
                Toast.LENGTH_SHORT).show();





        add_card_btn = findViewById(R.id.add_card_btn);
        add_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CreateCardActivity.class);
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

