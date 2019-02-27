package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stavroula.uber.entity.User;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends MainActivity {

    TextInputLayout til_username, til_first_name, til_last_name, til_email, til_password, til_confirm_password, til_phone;
    TextInputEditText edt_username, edt_first_name, edt_last_name, edt_email, edt_password, edt_confirm_password, edt_phone;
    Button registerbtn, have_an_account_btn;

    TextView mResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        til_username =  findViewById(R.id.til_username);
        til_first_name = findViewById(R.id.til_first_name);
        til_last_name = findViewById(R.id.til_last_name);
        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);
        til_confirm_password = findViewById(R.id.til_confirm_password);
        til_phone = findViewById(R.id.til_phone);

        edt_username = findViewById(R.id.edt_username);
        edt_first_name = findViewById(R.id.edt_first_name);
        edt_last_name = findViewById(R.id.edt_last_name);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_confirm_password = findViewById(R.id.edt_confirm_password);
        edt_phone =  findViewById(R.id.edt_phone);

        mResponse = findViewById(R.id.mresponse);

        registerbtn = findViewById(R.id.rider_register_button);
        have_an_account_btn = findViewById(R.id.have_an_account_button);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt_username.getText().toString();
                String first_name = edt_first_name.getText().toString();
                String last_name = edt_last_name.getText().toString();
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();
                String phone = edt_phone.getText().toString();

                User user = new User();
                user.setUsername(username);
                user.setFirstName(first_name);
                user.setLastName(last_name);
                user.setEmail(email);
                user.setPassword(password);
                user.setPhoneNumber(phone);

                sign_up(user);
            }
        });

        have_an_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rider_login();
            }
        });
    }

    private void sign_up(User user) {

        Log.d("123", "user;"+ user.toString());
        String url = "http://192.168.1.9:8080/";
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

        Call<User> call =  apiService.createRider(user);
        Log.d("123", "call"+call.toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
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
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

        Toast.makeText(SignUpActivity.this,"Your username is "+user.getUsername(),
                Toast.LENGTH_SHORT).show();
    }

    public void rider_login(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login,null);
        Intent intent = new Intent(SignUpActivity.this,LogInActivity.class);
        startActivity(intent);
    }

    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }
}