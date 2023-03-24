package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.stavroula.uber.entity.User;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends MainActivity {

    TextInputLayout til_username, til_email, til_password;
    TextInputEditText edt_username, edt_email, edt_password;
    Button registerbtn;

    TextView mResponse, loginbtn;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    ProgressBar progressBar;

    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        til_username =  findViewById(R.id.til_username);
        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);


        edt_username = findViewById(R.id.edt_username);

        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);


        registerbtn = findViewById(R.id.rider_register_button);
        loginbtn = findViewById(R.id.login_button);

        progressBar = findViewById(R.id.register_bar);
        progressBar.setVisibility(View.INVISIBLE);

        //Already have an account -> Redirection to Login page
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Register.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edt_username.getText().toString();
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please enter username...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
                    return;
                }

                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);

                register(username,email,password);

                Log.wtf("123", "user;"+ currentUser);
            }
        });

        auth = FirebaseAuth.getInstance();

    }

    private void register(final String username, final String email, final String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String , String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username",username);
                    hashMap.put("email", email);
                    hashMap.put("password", password);

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                mUser.getIdToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    String Uid = FirebaseAuth.getInstance().getUid();
                                                    Log.wtf("123", "UID;"+ Uid);

                                                    User user = new User();
                                                    user.setUsername(username);
                                                    user.setEmail(email);
                                                    user.setPassword(password);
                                                    user.setFirebase_token(Uid);
                                                    sign_up(user);
                                                    // Redirection to RiderMap Activity
                                                    Intent intent = new Intent (Register.this, RiderMapActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    // Handle error -> task.getException();
                                                    String localizedMessage = task.getException().getLocalizedMessage();
                                                    showMessage(localizedMessage);
                                                    Log.wtf("123", "createUserWithEmail:failure", task.getException());
                                                    Log.wtf("123", "LOCALIZEDMESSAGE"+localizedMessage);
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                                //progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
                else {
                    String localizedMessage = task.getException().getLocalizedMessage();
                    showMessage(localizedMessage);
                    Log.wtf("123", "createUserWithEmail:failure", task.getException());
                    Log.wtf("123", "LOCALIZEDMESSAGE"+localizedMessage);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void sign_up(User user){

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
                    Log.d("123", "response"+response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

        Toast.makeText(Register.this,"Your username is "+user.getUsername(),
                Toast.LENGTH_SHORT).show();
    }

    /*public void rider_login(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login,null);
        Intent intent = new Intent(SignUpActivity.this,LogInActivity.class);
        startActivity(intent);
    }*/
    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();
    }

}