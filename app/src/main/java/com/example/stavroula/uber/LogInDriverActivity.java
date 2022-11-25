package com.example.stavroula.uber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import static com.example.stavroula.uber.R.drawable.ic_menu;

public class LogInDriverActivity extends MainActivity  {

    Button loginbtn, need_an_account_btn;

    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner_item);

        DrawerLayout drawerLayout = findViewById(R.id.driver_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav_menu, R.string.close_nav_menu);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        Toolbar toolbar = findViewById(R.id.driver_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();


        actionbar.setTitle(0);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(ic_menu);

        NavigationView navigationView = findViewById(R.id.driver_nav_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_vehicle:
                        Toast.makeText(LogInDriverActivity.this, "Payment", Toast.LENGTH_SHORT).show();
                        Intent intent1= new Intent(LogInDriverActivity.this, VehiclesActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_history:
                        Toast.makeText(LogInDriverActivity.this, "History", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(LogInDriverActivity.this, ProfileActivity.class);
                        startActivity(intent2);

                        break;
                    case R.id.nav_profile:
                        Toast.makeText(LogInDriverActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(LogInDriverActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_log_out:
                        AlertDialog.Builder builder=new AlertDialog.Builder(LogInDriverActivity.this);
                        builder.setMessage("Do you want to exit?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO check if there is a better solution
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    finishAffinity();
                                }
                            }
                        });

                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert=builder.create();
                        alert.show();
                        break;
                }
               return  false;

            }
            });
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.drawerLayout.openDrawer(GravityCompat.START);
                drawerLayout.bringToFront();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }








}