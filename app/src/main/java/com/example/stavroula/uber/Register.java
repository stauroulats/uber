package com.example.stavroula.uber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.stavroula.uber.entity.User;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends MainActivity {

    TextInputLayout til_username, til_email, til_password;
    TextInputEditText edt_username, edt_email, edt_password;
    Button registerbtn;

    TextView mResponse;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");



        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edt_username.getText().toString();
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();


                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);

                //TODO if email matches with existing email / else if password.length < x characters
                sign_up(user);

                Log.wtf("123", "user;"+ currentUser);
            }
        });

        auth = FirebaseAuth.getInstance();

    }

    private void register(String username, final String email, final String password) {

        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String , String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("email", email);
                    hashMap.put("password", password);

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                //Log.wtf("123", "user;"+ currentuser);
                                Intent intent = new Intent (Register.this, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }
            }
        });

    }

    private void sign_up(User user){
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

}