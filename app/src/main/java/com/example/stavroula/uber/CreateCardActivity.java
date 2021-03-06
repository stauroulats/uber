package com.example.stavroula.uber;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stavroula.uber.entity.CreditCard;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCardActivity extends MainActivity {

    TextInputLayout til_card_number, til_cardholder_name, til_cvv, til_year, til_month;
    TextInputEditText edt_card_number, edt_cardholder_name, edt_cvv, edt_year, edt_month;
    Button save_btn;

    ImageButton return_back_btn;

    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_credit_card);


        til_card_number = findViewById(R.id.til_card_number);
        til_cardholder_name =  findViewById(R.id.til_cardholder_name);
        til_cvv =  findViewById(R.id.til_cvv);
        til_year =  findViewById(R.id.til_year);
        til_month =  findViewById(R.id.til_month);


        edt_card_number =  findViewById(R.id.edt_card_number);
        edt_cardholder_name =  findViewById(R.id.edt_cardholder_name);
        edt_cvv =  findViewById(R.id.edt_cvv);
        edt_year =  findViewById(R.id.edt_year);
        edt_month =  findViewById(R.id.edt_month);

        mResponse = findViewById(R.id.mresponse);

        //Return previous activity button
        return_back_btn =  findViewById(R.id.return_button);
        return_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save_btn = findViewById(R.id.save_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long card_number = Long.parseLong(edt_card_number.getText().toString());
                Log.wtf("123", "card_number"+card_number);
                String cardholder_name = edt_cardholder_name.getText().toString();
                Integer cvv = Integer.parseInt(edt_cvv.getText().toString());
                Integer year = Integer.parseInt(edt_year.getText().toString());
                Integer month = Integer.parseInt(edt_month.getText().toString());

                CreditCard creditCard = new CreditCard();
                creditCard.setNumber(card_number);
                creditCard.setName(cardholder_name);
                creditCard.setCvv(cvv);
                creditCard.setYear(year);
                creditCard.setMonth(month);

                createCreditCard(creditCard);
            }
        });

    }

   private void createCreditCard(CreditCard creditCard){

       ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
       Log.d("123", "apiservice"+apiService.toString());

       Call<CreditCard> call =  apiService.createCreditCard(creditCard);
       Log.d("123", "call"+call.toString());
       call.enqueue(new Callback<CreditCard>() {
           @Override
           public void onResponse(Call<CreditCard> call, Response<CreditCard> response) {
               Log.wtf("123", "response"+new Gson().toJson(response.body()));

               int msg =  response.code();
               Log.d("123", "message"+msg);

               if (response.isSuccessful()) {
                   showResponse(new Gson().toJson(response.body()));
                   Log.d("123", "response"+response.body().toString());
                   Log.d("123", "post submitted to API." + response.body().toString());
               }
           }

           @Override
           public void onFailure(Call<CreditCard> call, Throwable t) {
               Log.d("123", "Unable to submit post to API.");
           }
       });

       Toast.makeText(CreateCardActivity.this,"Your manufacture is  and model is ",
               Toast.LENGTH_SHORT).show();
   }

    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }
}


