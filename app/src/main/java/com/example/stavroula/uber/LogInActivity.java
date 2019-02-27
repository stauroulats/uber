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

import com.example.stavroula.uber.entity.LoginData;
import com.example.stavroula.uber.entity.User;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends MainActivity {

    TextInputLayout til_email, til_password;
    TextInputEditText edt_email, edt_password;
    Button loginbtn,need_an_account_btn;

    TextView mResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        til_email =  findViewById(R.id.til_email) ;
        til_password =  findViewById(R.id.til_password);

        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);

        loginbtn = findViewById(R.id.loginRbtn);
        need_an_account_btn = findViewById(R.id.need_an_account_btn);

        mResponse = (TextView) findViewById(R.id.mresponse);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();
                login(email,password);
            }
        });

        need_an_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up();
            }
        });
    }

    private void login(String email, String password) {


        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.wtf("123", "apiservice"+apiService.toString());

        Call<User> call =  apiService.loginUser(new LoginData(email, password));
        Log.wtf("123", "call"+call.toString());
                call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.wtf("123", "response"+new Gson().toJson(response.body()));

               int msg =  response.code();
                Log.wtf("123", "message"+msg);

                if (response.isSuccessful()) {
                   showResponse(new Gson().toJson(response.body()));
                    Log.wtf("123", "response"+response.body().toString());
                    Log.wtf("123", "post submitted to API." + response.body().toString());
                viewMap();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.wtf("123", "Unable to submit post to API.");
            }
        });

        Toast.makeText(LogInActivity.this,"Your email is and your password "+email + password,
                Toast.LENGTH_SHORT).show();
    }

    private void sign_up(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_register = inflater.inflate(R.layout.layout_login,null);
        Intent intent = new Intent(LogInActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    public void viewMap(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View map_layout = inflater.inflate(R.layout.activity_rider_maps,null);
        Intent intent = new Intent(LogInActivity.this,RiderMapActivity.class);
        startActivity(intent);
    }

    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }
}