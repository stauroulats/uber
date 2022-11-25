package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button rider_login_button, rider_register_button, driver_login_button, driver_register_button;

    Button test_btn,profile, history_btn, cars_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rider_register_button = (Button) findViewById(R.id.rider_register_button);
        rider_login_button = (Button) findViewById(R.id.rider_login_button);
        driver_login_button = (Button) findViewById(R.id.driver_login_button);
        driver_register_button = (Button) findViewById(R.id.driver_register_button);

        rider_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rider_login();
            }
        });

        rider_register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rider_register();
            }
        });

        driver_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver_login();
            }
        });

        driver_register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver_register();
            }
        });

        test_btn = findViewById(R.id.test_btn);
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        profile = findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile();
            }
        });

        history_btn = findViewById(R.id.history);
        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history();
            }
        });

        cars_btn = findViewById(R.id.cars_btn);
        cars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cars();
            }
        });

    }

    private void rider_login() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login,null);

        Toast.makeText(MainActivity.this,"Enter your email and passsword to LogIn",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,LogInActivity.class);
        startActivity(intent);

    }
    private void rider_register() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_register = inflater.inflate(R.layout.register,null);

        Toast.makeText(MainActivity.this,"Enter your personal info to SignUp",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,Register.class);
        startActivity(intent);

    }

    private void driver_login() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.navigation_drawer,null);

        Toast.makeText(MainActivity.this,"Enter your email and passsword to LogIn",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,LogInDriverActivity.class);
        startActivity(intent);

    }


    private void driver_register() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_register = inflater.inflate(R.layout.layout_drv_register,null);

        Toast.makeText(MainActivity.this,"Enter your personal info to SignUp",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
        startActivity(intent);

    }

    private void test(){
            LayoutInflater inflater = LayoutInflater.from(this);
           // View layout_register = inflater.inflate(R.layout.rtrip_history_layout,null);
            View layout_register = inflater.inflate(R.layout.driver_welcome_layout,null);
            Intent intent = new Intent(MainActivity.this,WelcomeDriverActivity.class);
            startActivity(intent);
    }

    private void cars(){
        LayoutInflater inflater = LayoutInflater.from(this);
        // View layout_register = inflater.inflate(R.layout.rtrip_history_layout,null);
        View layout_register = inflater.inflate(R.layout.layout_vehicle_list,null);
        Intent intent = new Intent(MainActivity.this,VehiclesActivity.class);
        startActivity(intent);
    }

    private void profile(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_register = inflater.inflate(R.layout.profile_layout,null);
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

    private void history(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View map_layout = inflater.inflate(R.layout.second_activity,null);
        Intent intent = new Intent(MainActivity.this,RiderMapActivity.class);
        startActivity(intent);

        /*LayoutInflater inflater = LayoutInflater.from(this);
        View history = inflater.inflate(R.layout.dtrip_history_layout,null);
        Intent intent = new Intent(MainActivity.this,DriverTripHistoryActivity.class);
        startActivity(intent);*/
    }


}

