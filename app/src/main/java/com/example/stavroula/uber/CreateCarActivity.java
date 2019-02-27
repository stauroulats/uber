package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stavroula.uber.entity.Car;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateCarActivity extends SignUpDriverActivity{

    TextInputLayout til_manufacture, til_model, til_year, til_registrationPlate, til_color;
    TextInputEditText edt_manufacture, edt_model, edt_year, edt_registrationPlate, edt_color;
    Button signUpbtn;

    ImageButton return_back_btn;

    TextView mResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_car);

        til_manufacture = findViewById(R.id.til_manufacture) ;
        til_model = findViewById(R.id.til_model);
        til_year = findViewById(R.id.til_year);
        til_registrationPlate = findViewById(R.id.til_registrationPlate);
        til_color = findViewById(R.id.til_color);

        edt_manufacture = findViewById(R.id.edt_manufacture);
        edt_model = findViewById(R.id.edt_model);
        edt_year = findViewById(R.id.edt_year);
        edt_registrationPlate = findViewById(R.id.edt_registrationPlate);
        edt_color = findViewById(R.id.edt_color);

        signUpbtn = findViewById(R.id.driver_register_button);


        mResponse = findViewById(R.id.mresponse);

        //Return previous activity button
        return_back_btn =  findViewById(R.id.return_button);
        return_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manufacture = edt_manufacture.getText().toString();
                String model = edt_model.getText().toString();
                String yearStr = edt_year.getText().toString();
                Integer year = Integer.parseInt(yearStr);
                String registrationPlate = edt_registrationPlate.getText().toString();
                String color = edt_color.getText().toString();

                Car car = new Car();
                car.setManufacturer(manufacture);
                car.setModel(model);
                car.setYear(year);
                car.setRegistrationPlate(registrationPlate);
                car.setColor(color);

                createCar(car);
            }
        });


    }

    private void createCar(Car car) {

        Log.d("123", "car"+ car);
        String url = "http://192.168.1.4:8080/";
        Log.d("123", "http://localhost/");
        Retrofit retrofit = null;
        Log.d("123", "retrofit");

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Log.d("123", "build();"+retrofit.toString());
        }

        ApiService apiService = retrofit.create(ApiService.class);
        Log.d("123", "apiservice"+apiService.toString());

        Call<Car> call =  apiService.createCar(car);
        Log.d("123", "call"+call.toString());
        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                Log.d("123", "response"+new Gson().toJson(response.body()));

                int msg =  response.code();
                Log.d("123", "message"+msg);

                if (response.isSuccessful()) {
                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response"+response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

        Toast.makeText(CreateCarActivity.this,"Your manufacture is  and model is ",
                Toast.LENGTH_SHORT).show();
    }

    private void sign_up(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_register = inflater.inflate(R.layout.layout_login,null);
        Intent intent = new Intent(CreateCarActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }

}
