package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class SignUpActivity extends MainActivity {

    TextInputLayout til_username, til_first_name, til_last_name, til_email, til_password, til_confirm_password, til_phone, til_address;
    TextInputEditText edt_username, edt_first_name, edt_last_name, edt_email, edt_password, edt_confirm_password, edt_phone, edt_address;
    Button registerbtn, have_an_account_btn;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    Boolean isAllFieldsChecked;

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
        til_address = findViewById(R.id.til_address);

        edt_username = findViewById(R.id.edt_username);
        edt_first_name = findViewById(R.id.edt_first_name);
        edt_last_name = findViewById(R.id.edt_last_name);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_confirm_password = findViewById(R.id.edt_confirm_password);
        edt_phone =  findViewById(R.id.edt_phone);
        edt_address = findViewById(R.id.edt_address);

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
                String confirm_password = edt_confirm_password.getText().toString();
                String prefix = til_phone.getPrefixText().toString();
                String user_phone = edt_phone.getText().toString();
                String phone = prefix+user_phone;
                Log.d("123", "phone"+phone);

                String address = edt_address.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please enter username...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(first_name)) {
                    Toast.makeText(getApplicationContext(), "Please enter first_name!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(last_name)) {
                    Toast.makeText(getApplicationContext(), "Please enter last_name...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(email) || !email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password...", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (TextUtils.getTrimmedLength(password)<8 ){
                    Toast.makeText(getApplicationContext(), "Password length should be > 8...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirm_password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (TextUtils.getTrimmedLength(password)<8 ){
                    Toast.makeText(getApplicationContext(), "Password length should be > 8...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.equals(password,confirm_password)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(phone) || TextUtils.getTrimmedLength(phone)<10 || TextUtils.getTrimmedLength(phone)>10 || !(phone.matches(("[0-9]+")))) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid phone number...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(), "Please enter address!", Toast.LENGTH_LONG).show();
                    return;
                }

            User user = new User();
                user.setUsername(username);
                user.setFirstName(first_name);
                user.setLastName(last_name);
                user.setEmail(email);
                user.setPassword(password);
                user.setPhoneNumber(phone);
                user.setAddress(address);

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