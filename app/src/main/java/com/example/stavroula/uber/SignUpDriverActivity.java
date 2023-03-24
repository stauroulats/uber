package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stavroula.uber.entity.User;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpDriverActivity extends MainActivity {

    TextInputLayout til_username, til_first_name, til_last_name, til_email, til_password, til_confirm_password, til_phone;
    TextInputEditText edt_username, edt_first_name, edt_last_name, edt_email, edt_password, edt_confirm_password, edt_phone;
    Button have_an_account_btn,next_btn;

    TextView mResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drv_register);

        til_username =  findViewById(R.id.til_username);
        til_first_name =  findViewById(R.id.til_first_name);
        til_last_name =  findViewById(R.id.til_last_name);
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
        edt_phone = findViewById(R.id.edt_phone);

        mResponse = findViewById(R.id.mresponse);

        next_btn = findViewById(R.id.next_btn);
        have_an_account_btn = findViewById(R.id.have_an_account_button);

        next_btn.setOnClickListener(new View.OnClickListener() {
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
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice"+apiService.toString());

        Call<User> call =  apiService.createDriver(user);
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
                    startActivity(new Intent(SignUpDriverActivity.this,CreateCarActivity.class));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

        Toast.makeText(SignUpDriverActivity.this,"Your username is "+user.getUsername(),
                Toast.LENGTH_SHORT).show();
        create_car();
    }

    public void rider_login(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login,null);
        Intent intent = new Intent(SignUpDriverActivity.this,LogInActivity.class);
        startActivity(intent);
    }

    public void create_car(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_car,null);
        Intent intent = new Intent(SignUpDriverActivity.this,CreateCarActivity.class);
        startActivity(intent);
    }

    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }
}