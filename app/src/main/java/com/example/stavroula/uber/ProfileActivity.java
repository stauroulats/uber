package com.example.stavroula.uber;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stavroula.uber.entity.Rider;
import com.example.stavroula.uber.entity.User;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends MainActivity {

    TextInputLayout til_username, til_first_name, til_last_name, til_email, til_password, til_phone , til_address , til_zip_code;
    TextInputEditText edt_username, edt_first_name, edt_last_name, edt_email, edt_password, edt_phone, edt_address , edt_zip_code;
    ImageButton profile_photo,change_password,close_btn;
    Button save_btn,change_password_btn;

    Spinner gender_spinner,country_spinner;

    TextView name,mResponse;

    EditText edt_new_password, edt_retype_password;

    PopupWindow pw;

    RelativeLayout relativeLayout;


    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mResponse = findViewById(R.id.mresponse);

        name = findViewById(R.id.name);

        til_username =  findViewById(R.id.til_username);
        til_first_name =  findViewById(R.id.til_first_name);
        til_last_name =  findViewById(R.id.til_last_name);
        til_email = findViewById(R.id.til_email);
        til_password = findViewById(R.id.til_password);
        til_phone = findViewById(R.id.til_phone);
        til_address = findViewById(R.id.til_address);
        til_zip_code = findViewById(R.id.til_zip_code);

        edt_username = findViewById(R.id.edt_username);
        edt_first_name = findViewById(R.id.edt_first_name);
        edt_last_name = findViewById(R.id.edt_last_name);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_phone = findViewById(R.id.edt_phone);
        edt_address = findViewById(R.id.edt_address);
        edt_zip_code = findViewById(R.id.edt_zip_code);


        mResponse = findViewById(R.id.mresponse);

        gender_spinner = findViewById(R.id.gender_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
       // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // Apply the adapter to the spinner
        gender_spinner.setAdapter(adapter);



        country_spinner = findViewById(R.id.country_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        country_spinner.setAdapter(adapter2);


        profile_photo = findViewById(R.id.profile_photo);
        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_photo();
            }
        });
        change_password = findViewById(R.id.change_password);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_password();
            }
        });

        save_btn = findViewById(R.id.save_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final User updatedUser = new User();
                updatedUser.setUsername(edt_username.getText().toString());
                updatedUser.setFirstName(edt_first_name.getText().toString());
                updatedUser.setLastName(edt_last_name.getText().toString());
                updatedUser.setEmail(edt_email.getText().toString());
                updatedUser.setFirstName(edt_first_name.getText().toString());
                updatedUser.setPhoneNumber(edt_phone.getText().toString());
                updatedUser.setAddress(edt_address.getText().toString());

                Log.wtf("123", "USER"+ edt_username.getText().toString());
                updateUser(updatedUser);
            }
        });



        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice" + apiService.toString());

        Call<Rider> call = apiService.getRider();
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<Rider>() {
            @Override
            public void onResponse(Call<Rider> call, Response<Rider> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    int statusCode = response.code();
                    Rider rider= response.body();

                    name.setText(rider.getUser().getFirstName()+ " " +rider.getUser().getLastName());

                    edt_username.setText(rider.getUser().getUsername());
                    edt_first_name.setText(rider.getUser().getFirstName());
                    edt_last_name.setText(rider.getUser().getLastName());
                    edt_email.setText(rider.getUser().getEmail());
                    edt_password.setText(rider.getUser().getPassword());
                    edt_phone.setText(rider.getUser().getPhoneNumber());
                    edt_address.setText(rider.getUser().getAddress());

                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response" + response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Rider> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

    }

    private void choose_photo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }


    //Opens gallery to pick up an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            ImageView image;
            Bitmap bitmap = null;

            if (Build.VERSION.SDK_INT >= 29) {
                ImageDecoder.Source source = ImageDecoder.createSource(getApplicationContext().getContentResolver(), selectedImage);
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        /*    image.setImageBitmap(bitmap); */
//TO DE DELETED
        /*    try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } */

       /*    Photo photo = new Photo(bitmap);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photo);
            MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", photo.getName(), requestBody);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Log.d("123", "apiservice" + apiService.toString());

            Call<User> call = apiService.updateUser(user);
            Log.d("123", "call" + call.toString());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.wtf("123", "response" + new Gson().toJson(response.body()));

                    int msg = response.code();
                    Log.d("123", "message" + msg);

                    if (response.isSuccessful()) {

                        Toast.makeText(call.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        int statusCode = response.code();
                        User user= response.body();

                        showResponse(new Gson().toJson(response.body()));
                        Log.d("123", "response" + response.body().toString());
                        Log.d("123", "post submitted to API." + response.body().toString());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("123", "Unable to submit post to API.");
                }
            }); */
        }
    }



    private void change_password() {
            try {

                LayoutInflater inflater = LayoutInflater.from(this);
                //Inflate the view from a predefined XML layout
                View layout = inflater.inflate(R.layout.pop_up_change_password_layout,null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

               relativeLayout = layout.findViewById(R.id.pop_up_window);
                // create a PopupWindow
                pw = new PopupWindow(this);
                pw.setContentView(layout);

                pw.setFocusable(true);
                pw.update();
                // display the popup in the center
                pw.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);


                edt_new_password = layout.findViewById(R.id.edt_new_password);
                edt_retype_password = layout.findViewById(R.id.edt_retype_password);
                change_password_btn = layout.findViewById(R.id.change_password_btn);


                close_btn = layout.findViewById(R.id.close_btn);
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pw.dismiss();
                    }
                });

                // dismiss the popup window when touched
                layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        pw.dismiss();
                        return true;
                    }
                });

                change_password_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO security check
                        if(((edt_new_password.getText().toString()).equals(edt_retype_password.getText().toString())) & (!edt_new_password.getText().toString().isEmpty())) {
                            User user = new User();
                            user.setPassword(edt_new_password.getText().toString());
                            updateUserPassword(user);
                        }
                        else if (edt_new_password.getText().toString().isEmpty()){
                            Toast.makeText(ProfileActivity.this, "Please enter a value!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(ProfileActivity.this, "Passwords do not match,please retype!", Toast.LENGTH_LONG).show();
                            edt_new_password.getText().clear();
                            edt_retype_password.getText().clear();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    private void updateUser(User user){

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice" + apiService.toString());

        Call<User> call = apiService.updateUser(user);
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    int statusCode = response.code();
                    User user= response.body();

                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response" + response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
            }
        });

    }

    private void updateUserPassword(User user){

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice" + apiService.toString());

        Call<User> call = apiService.updateUserPassword(user);
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    Toast.makeText(ProfileActivity.this, "Your password has been changed successfully!", Toast.LENGTH_LONG).show();

                    int statusCode = response.code();
                    User user= response.body();
                    showResponse(new Gson().toJson(response.body()));
                    Log.d("123", "response" + response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
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

