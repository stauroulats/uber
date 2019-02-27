package com.example.stavroula.uber;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stavroula.uber.entity.TripStatistics;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverTripHistoryActivity extends MainActivity {

    DatePickerDialog datePickerDialog;
    ImageButton return_btn;
    TextView date_txt,total_trips_txt, km_txt, earnings_txt;
    Button week_statistics, month_statistics;

    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dtrip_history_layout);

        mResponse = findViewById(R.id.mresponse);

        date_txt = findViewById(R.id.date);

        total_trips_txt = findViewById(R.id.total_trips_value);
        km_txt = findViewById(R.id.km_value);
        earnings_txt = findViewById(R.id.earnings_value);

        DatePicker datePicker = findViewById(R.id.datePicker);


        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener(){
            @Override
                    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        Log.wtf("123","Date"+ "Year=" + year + " Month=" + (monthOfYear + 1) + " day=" + dayOfMonth);
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE",Locale.ENGLISH);
                        //String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
                        Date date = new Date(year, monthOfYear, dayOfMonth-1);
                        String dayOfWeek = simpledateformat.format(date);
                        String date_str = (dayOfWeek+"," +" "+dayOfMonth + " " + (monthOfYear+1) + " " + year);
                         Log.wtf("123","Date"+ date_str);
                        String hello = (" " + dayOfWeek);
                         Log.wtf("123","Date"+ hello);
                        date_txt.setText(dayOfWeek+"," + "" +dayOfMonth + " " +(monthOfYear+1) + " " + year);

                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Log.d("123", "apiservice" + apiService.toString());

                Call<TripStatistics> call = apiService.getDayStatistics(date_str);
                Log.wtf("123", "call" + call.request().toString());
                call.enqueue(new Callback<TripStatistics>() {
                    @Override
                    public void onResponse(Call<TripStatistics> call, Response<TripStatistics> response) {
                        Log.wtf("123", "response" + new Gson().toJson(response.body()));

                        int msg = response.code();
                        Log.d("123", "message" + msg);

                        if (response.isSuccessful()) {

                            int statusCode = response.code();
                            TripStatistics statistics= response.body();

                            if (response.body()==null){
                                total_trips_txt.setText(" ");
                                km_txt.setText(" ");
                                earnings_txt.setText(" ");
                            }


                            total_trips_txt.setText(String.valueOf(statistics.getTotalTrips()));
                            km_txt.setText(String.valueOf(statistics.getTotalKms()));
                            earnings_txt.setText(String.valueOf(statistics.getEarnings()));

                            showResponse(new Gson().toJson(response.body()));
                            Log.d("123", "response" + response.body().toString());
                            Log.d("123", "post submitted to API." + response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<TripStatistics> call, Throwable t) {
                        Log.wtf("123", "Unable to submit post to API.");
                    }
                });

            }
                });

        return_btn = findViewById(R.id.return_button);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }


    public void showResponse (String response){
        if (mResponse.getVisibility() == View.GONE) {
            mResponse.setVisibility(View.VISIBLE);
        }
        mResponse.setText(response);
    }


    }

