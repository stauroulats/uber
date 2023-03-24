package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.stavroula.uber.entity.LoginData;
import com.example.stavroula.uber.entity.User;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends MainActivity {

    TextInputLayout til_email, til_password;
    TextInputEditText edt_email, edt_password;
    Button loginbtn,need_an_account_btn;
    ProgressBar loginProgress;

    TextView mResponse;

    FirebaseAuth mAuth;


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

        loginProgress = findViewById(R.id.login_progress);
        mResponse = (TextView) findViewById(R.id.mresponse);

        loginProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgress.setVisibility(View.INVISIBLE);

                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();
                if ( email.isEmpty() || password.isEmpty() ) {
                    // Error : all fields must be filled
                    // Display an error message
                    showMessage("Please Verify all fields");
                    mResponse.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else {
                    login(email,password);
                    Intent intent = new Intent (LogInActivity.this, LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        need_an_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up();
            }
        });
    }

    private void FirebaseLogin(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginbtn.setVisibility(View.VISIBLE);
                    login(email,password);

                }
                else {
                    showMessage(task.getException().getMessage());
                    loginbtn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }
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
                showMessage("Your email is and your password" +email + password);

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

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();
    }
}