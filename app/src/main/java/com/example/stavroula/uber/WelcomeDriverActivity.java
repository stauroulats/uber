package com.example.stavroula.uber;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.stavroula.uber.entity.Car;
import com.example.stavroula.uber.entity.Driver;
import com.example.stavroula.uber.entity.TripRequest;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WelcomeDriverActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {


    ImageButton close_btn;

    private RelativeLayout relativeLayout, mrelativeLayout;

    PopupWindow pw;

    private Marker animationMarker;

    private GoogleMap mMap;

    private Marker startPositionMarker;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final int DEFAULT_ZOOM = 15;

    private CameraPosition mCameraPosition;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;
    private ArrayList markerPoints= new ArrayList();

    private String KEY = "AIzaSyCBzOeULa1Vfi3qBuuwRVei7O8rqT_BLJI";

    private List<LatLng> polyLineList;

    public String parsedDistance;

    private PolylineOptions polylineoptions, blackpolylineoptions;
    private Polyline blackpolyline, greypolyline;

    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;

    private float v;
    private double lat, lng;

    ImageView rider_img;
    TextView riderName_txt, rating_txt;
    Button call_btn, chat_btn, cancel_btn, start_trip_btn;

    Dialog dialog;

    private FusedLocationProviderClient mFusedLocationClient; // Object used to receive location updates

    private LocationRequest locationRequest; // Object that defines important parameters regarding location request.

    private final String TOPIC = "tripRequest";
    private final String TRIPTOPIC = "trip";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_welcome_layout);


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a FusedLocationProviderClient. New add on
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        sendUpdatedLocation();

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "acceptTripNotification".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("acceptTripNotification"));

        LocalBroadcastManager.getInstance(this).registerReceiver(cancelMessageReceiver,
                new IntentFilter("cancelTripNotification"));

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        mapFragment.getMapAsync(WelcomeDriverActivity.this);
        Log.wtf("123", "LOCATION" +mLastKnownLocation);


        Switch status_switch = findViewById(R.id.status_switch); // initiate Switch
        status_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribed";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe failed";
                            }
                            Log.d("123", msg);
                            Toast.makeText(WelcomeDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                    Driver driver = new Driver();
                    driver.setActive(true);
                    updateStatus(true);
                }else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                    Driver driver = new Driver();
                    driver.setActive(true);
                    updateStatus(false);
                }

            }
        });

        //TODO if driver.selectedCar=!null then popUpwindow(in case driver redirects from decline/accept,dont throw popUp again)
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<List<Car>> call = apiService.getCars();
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {

                    int statusCode = response.code();
                    List<Car> cars = response.body();

                    if (cars.size()==1) {
                        Toast.makeText(WelcomeDriverActivity.this,"Your selected car is:" + cars.get(0).getManufacturer(), Toast.LENGTH_LONG).show();
                    }
                    else {
                       /*initiatePopupWindow(cars);*/}
                    }
                }


            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");

            }
        });


    }

    private void sendUpdatedLocation() {
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String latitudeField = (String.valueOf(latitude));
                    String longitudeField = (String.valueOf(longitude));
                    //String locationToParse = String.valueOf(latitudeField);
                    Log.d("123", "LOCATION" + latitudeField);
                    Log.d("123", "LOCATION" + longitudeField);
                    //Update Driver's location
                    ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                    Log.d("123", "apiservice" + apiService.toString());
                    Call<String> call = apiService.updateLocation(latitude,longitude);
                    Log.d("123", "call" + call.toString());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.wtf("123", "response" + new Gson().toJson(response.body()));
                            int msg = response.code();
                            Log.d("123", "message" + msg);
                            if (response.isSuccessful()) {
                                int statusCode = response.code();
                                Log.d("123", "response" + response.body().toString());
                                Log.d("123", "post submitted to API." + response.body().toString());
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("123", "Unable to submit post to API.");
                        }
                    });
                }
            }, Looper.myLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationClickListener(this);

        Log.wtf("Maps", "Map is ready");


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    private void createRoute(final Long tripRequestId) {
        //Retrieve tripRequestId from RequestCallActivity --> notification data
            final LatLng riderPosiiton = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
            final String origin =getCompleteAddress(riderPosiiton.latitude,riderPosiiton.longitude);
            Log.wtf("123", "riderposition" + riderPosiiton);
            Log.wtf("123", "origin "+origin);

            final ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            Call<TripRequest> call = apiService.getTripRequest(tripRequestId);
            Log.d("123", "call" + call.toString());
            call.enqueue(new Callback<TripRequest>() {
                @Override
                public void onResponse(Call<TripRequest> call, Response<TripRequest>  response) {
                    Log.wtf("123", "response" + new Gson().toJson(response.body()));
                    int msg = response.code();
                    Log.d("123", "message" + msg);
                    String url = "https://maps.googleapis.com/";
                    Log.wtf("123", "http://maps/api/directions/");
                    Retrofit retrofit = null;
                    Log.d("123", "retrofit");

                    if (retrofit == null) {
                        retrofit = new Retrofit.Builder()
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        Log.wtf("123", "build();"+retrofit.toString());
                    }

                    ApiService apiService2 = retrofit.create(ApiService.class);

                    if (response.isSuccessful()) {
                        int statusCode = response.code();
                        final TripRequest tripRequest = response.body();
                        final String pickUpPoint = tripRequest.getPickUpPoint();
                        final String riderDestination = tripRequest.getDestination();
                        Log.wtf("123", "pickup" + pickUpPoint);
                        LatLng address = getLocationFromAddress(getApplicationContext(),pickUpPoint);
                        Log.wtf("123", "address" + address.latitude+" " +address.longitude);

                        //mMap.addMarker(new MarkerOptions().position(new LatLng(address.latitude,address.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                        Call<JsonObject> call2 =  apiService2.getDirections("driving","less_driving",origin,pickUpPoint,KEY);
                        Log.wtf("123", "call2"+call2.request().toString());
                        call2.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call2, Response<JsonObject> response) {
                                Log.wtf("123", "response" + response.raw().message());

                                int msg = response.code();
                                Log.d("123", "message" + msg);

                                if (response.isSuccessful()) {
                                    Log.wtf("123", "response" + response.body());
                                    try {

                                        if (blackpolyline != null) {
                                            greypolyline.remove();
                                            blackpolyline.remove();
                                        }
                                        JSONObject jsonobject = new JSONObject(response.body().toString());
                                        Log.wtf("123", "ROUTE" + jsonobject.toString());
                                        JSONArray jsonArray = jsonobject.getJSONArray("routes");

                                        //Find the distance to show the estimated rate
                                        JSONObject routes = jsonArray.getJSONObject(0);
                                        JSONArray legs = routes.getJSONArray("legs");
                                        JSONObject leg = legs.getJSONObject(0);
                                        JSONObject endLocation = leg.getJSONObject("end_location");
                                        String endLocationLatitude = endLocation.getString("lat");
                                        String endLocationLongitude = endLocation.getString("lng");
                                        Log.wtf("123", "latlng" + endLocationLatitude+ " " + endLocationLongitude);
                                        final LatLng latLng = new LatLng(Double.parseDouble(endLocationLatitude),Double.parseDouble(endLocationLongitude));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(Double.parseDouble(endLocationLatitude),Double.parseDouble(endLocationLongitude)), DEFAULT_ZOOM));
                                        /*Marker car icon*/
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(endLocationLatitude),Double.parseDouble(endLocationLongitude)))
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                        markerPoints.add(latLng);
                                        JSONObject steps = legs.getJSONObject(0);
                                        JSONObject distance = steps.getJSONObject("distance");

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject route = jsonArray.getJSONObject(i);
                                            JSONObject poly = route.getJSONObject("overview_polyline");
                                            String polyline = poly.getString("points");
                                            Log.wtf("123", "polyline" + polyline);
                                            polyLineList = decodePoly(polyline);
                                            Log.wtf("123", "polyline" + polyline);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (LatLng latLng : polyLineList)
                                        builder.include(latLng);
                                    LatLngBounds bounds = builder.build();
                                    CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                    mMap.animateCamera(mCameraUpdate);

                                    //Draw the route line
                                    polylineoptions = new PolylineOptions();
                                    polylineoptions.color(Color.GRAY);
                                    polylineoptions.width(5);
                                    polylineoptions.startCap(new SquareCap());
                                    polylineoptions.endCap(new SquareCap());
                                    polylineoptions.jointType(JointType.ROUND);
                                    polylineoptions.addAll(polyLineList);
                                    greypolyline = mMap.addPolyline(polylineoptions);


                                    blackpolylineoptions = new PolylineOptions();
                                    blackpolylineoptions.color(Color.BLACK);
                                    blackpolylineoptions.width(5);
                                    blackpolylineoptions.startCap(new SquareCap());
                                    blackpolylineoptions.endCap(new SquareCap());
                                    blackpolylineoptions.jointType(JointType.ROUND);
                                    blackpolylineoptions.addAll(polyLineList);
                                    blackpolyline = mMap.addPolyline(blackpolylineoptions);

                                }

                               //Animate the line route
                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = greypolyline.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackpolyline.setPoints(p);
                                    }
                                });
                                polylineAnimator.start();


                                //Animation of the route from start to end

                                animationMarker = mMap.addMarker(new MarkerOptions().position(riderPosiiton)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (index < polyLineList.size() - 1) {
                                            index++;
                                            next = index + 1;
                                        }
                                        if (index < polyLineList.size() - 1) {
                                            startPosition = polyLineList.get(index);
                                            endPosition = polyLineList.get(next);
                                        }
                                        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                        valueAnimator.setDuration(3000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.setRepeatCount(0);
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                v = valueAnimator.getAnimatedFraction();
                                                lng = v * endPosition.longitude + (1 - v)
                                                        * startPosition.longitude;
                                                lat = v * endPosition.latitude + (1 - v)
                                                        * startPosition.latitude;
                                                LatLng newPos = new LatLng(lat, lng);
                                                animationMarker.setPosition(newPos);
                                                animationMarker.setAnchor(0.5f, 0.5f);
                                                animationMarker.setRotation(getBearing(startPosition, newPos));
                                                float rotation = getBearing (startPosition, newPos);
                                                if (animationMarker.getRotation ()> rotation){
                                                    animationMarker.setRotation (animationMarker.getRotation () - valueAnimator.getAnimatedFraction ());}
                                                else{
                                                    animationMarker.setRotation (animationMarker.getRotation () + valueAnimator.getAnimatedFraction ());}
                                                mMap.moveCamera(CameraUpdateFactory
                                                        .newCameraPosition
                                                                (new CameraPosition.Builder()
                                                                        .target(newPos)
                                                                        .zoom(15.5f)
                                                                        .build()));
                                            }
                                        });
                                        valueAnimator.start();
                                        handler.postDelayed(this, 3000);
                                  /*      if (index == (polyLineList.size()-1)) {
                                            valueAnimator.removeAllUpdateListeners();
                                            Log.wtf("123", "end route");
                                            handler.removeCallbacksAndMessages(null);
                                            pw.dismiss();
                                            //Start Trip Button
                                            dialog = new Dialog(WelcomeDriverActivity.this);
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.getWindow().setBackgroundDrawable(
                                                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                            dialog.setContentView(R.layout.dialog_layout);
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.setCancelable(true);
                                            start_trip_btn = dialog.findViewById(R.id.start_trip_btn);

                                            start_trip_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //TODO startTrip
                                                    dialog.cancel();
                                                    // Start Trip Request
                                                    ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

                                                    Call<Trip> call = apiService.startTrip(tripRequestId);
                                                    call.enqueue(new Callback<Trip>() {
                                                        @Override
                                                        public void onResponse(Call<Trip> call, Response<Trip> response) {
                                                            if (response.isSuccessful()){
                                                                final Long tripId = response.body().getId();
                                                                Log.wtf("123", "Got notification:Driver cancel");
                                                                Log.wtf("123", "RIDERPOSITION" +riderDestination + " " + pickUpPoint);
                                                                Intent intent = new Intent(WelcomeDriverActivity.this, TripActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                intent.putExtra("riderPosition", origin);
                                                                intent.putExtra("riderDestination", riderDestination);
                                                                intent.putExtra("tripId", tripId);
                                                                // intent.putExtra("data2", );
                                                                startActivity(intent);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Trip> call, Throwable t) {

                                                        }
                                                    });

                                                }
                                            });

                                            dialog.show();
                                            sendArrivalMessage();
                                        } */
                                    }


                                }, 3000);

                            }
                            @Override
                            public void onFailure(Call<JsonObject> call2, Throwable t) {
                                Log.wtf("123", "Unable to submit post to API.");
                            }

                        });

                    }
                }
                @Override
                public void onFailure(Call<TripRequest>  call, Throwable t) {
                    Log.d("123", "Unable to submit post to API.");
                }
            });

    }



    private void sendNotification(Long driverId) {

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.wtf("123", "Permission Granted");
        } else {// Build the map.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.wtf("123", "Permission not Granted");
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                Log.wtf("123", "Current location" + locationResult.toString());
                locationResult.addOnCompleteListener(WelcomeDriverActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Log.wtf("123", "mLastKnownLocation "+mLastKnownLocation);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            double latidute = mLastKnownLocation.getLatitude();
                            double longitude = mLastKnownLocation.getLongitude();
                            /*Marker car icon*/
                           startPositionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                           String latLngToParse = String.valueOf(latLng);
                            Log.d("123", "latLNG" + latLngToParse);
                            //Update Driver's location
                            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                            Log.d("123", "apiservice" + apiService.toString());

                            Call<String> call = apiService.updateLocation(latidute, longitude);
                            Log.d("123", "call" + call.toString());
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Log.wtf("123", "response" + new Gson().toJson(response.body()));

                                    int msg = response.code();
                                    Log.d("123", "message" + msg);

                                    if (response.isSuccessful()) {

                                        int statusCode = response.code();
                                        Log.d("123", "response" + response.body().toString());
                                        Log.d("123", "post submitted to API." + response.body().toString());
                                    }
                                }
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("123", "Unable to submit post to API.");
                                }
                            });

                            // Adding new item to the ArrayList
                            markerPoints.add(latLng);

                        } else {
                            Log.wtf("123", "Current location is null. Using defaults.");
                            Log.wtf("123", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
        Log.wtf("123", "mLastKnownLocation "+mMap.getMyLocation());

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        String address = getCompleteAddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        Log.wtf("123", "mLastKnownLocation "+mLastKnownLocation.getLatitude()+mLastKnownLocation.getLongitude());
        Toast.makeText(this, "Current location:\n" + address, Toast.LENGTH_LONG).show();
    }


    @SuppressLint("LongLogTag")
    private String getCompleteAddress(double LATITUDE, double LONGITUDE) {
        String address_str = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returned_Address = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returned_Address.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returned_Address.getAddressLine(i)).append("\n");
                }
                address_str = strReturnedAddress.toString();
                Log.d("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.d("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("My Current loction address", "Canont get Address!");
        }
        return address_str;
    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    private void initiatePopupWindow(List<Car> cars) {
        try {

            LayoutInflater inflater = LayoutInflater.from(this);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.pop_up_select_car_layout,null);

            relativeLayout = layout.findViewById(R.id.pop_up_window);
            // create a PopupWindow
            pw = new PopupWindow(this);
            pw.setContentView(layout);
            // display the popup in the center
            pw.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);



            close_btn = findViewById(R.id.close_btn);

            RadioGroup rgp = layout.findViewById(R.id.radio_group);

            for (int i = 0; i < cars.size(); i++) {
                RadioButton rbn = new RadioButton(this);
                rbn.setText(cars.get(i).getManufacturer()+ cars.get(i).getModel() + cars.get(i).getRegistrationPlate());
                rbn.setButtonDrawable(R.drawable.car);
                //rbn.setBackgroundResource(R.drawable.car);
                rgp.addView(rbn);
            }


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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Unsubscribe from trip request TOPIC
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);

            FirebaseMessaging.getInstance().subscribeToTopic(TRIPTOPIC);

            // Get extra data included in the Intent
            Bundle bundle = intent.getExtras();
            Log.wtf("firstName", "Got message: " + bundle.get("tripRequestId"));
            Log.wtf("firstName", "Got message: " + bundle.get("riderName"));
            Log.wtf("firstName", "Got message: " + bundle.get("rating"));

            Long trip_id = bundle.getLong("tripRequestId");
            String riderName = bundle.getString("riderName");
            String rating = bundle.getString("rating");
            Log.wtf("firstName", "Got message: " + trip_id);
            Toast.makeText(WelcomeDriverActivity.this,"I receive the notification",Toast.LENGTH_LONG).show();
            initiateRiderInfoPopupWindow(riderName, rating, trip_id);
            createRoute(trip_id);


        }
    };

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver cancelMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Unsubscribe from trip TOPIC
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TRIPTOPIC);

            //Subscribe to trip request TOPIC
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

            // Get extra data included in the Intent
            Bundle bundle = intent.getExtras();
            String body =  bundle.getString("data1");

            //Alert box to inform rider that the driver has arrived
            new AlertDialog.Builder(WelcomeDriverActivity.this)
                    .setTitle("Trip Notification")
                    .setMessage(body)

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    //.setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_car)
                    .show();


        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    // Send an Intent with an action named "driver_arrived"
    private void sendArrivalMessage() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<Long> call = apiService.getDriverArrivalNotification();
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
            if (response.isSuccessful()){
                Log.wtf("123", "Got notification:Driver arrived");
            }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });
    }

// Pop Up Window with rider's information | Call | Chat | Cancel buttons
    private void initiateRiderInfoPopupWindow(String riderName, String rating, final Long tripRequestId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        //Inflate the view from a predefined XML layout
        View layout = inflater.inflate(R.layout.pop_up_trip_driver,null);

        mrelativeLayout = layout.findViewById(R.id.pop_up_window);
        // create a PopupWindow
        pw = new PopupWindow(this);
        pw.setContentView(layout);

        riderName_txt = layout.findViewById(R.id.name);
        rating_txt = layout.findViewById(R.id.rating);
        riderName_txt.setText(riderName);
        rating_txt.setText(rating);

        chat_btn = layout.findViewById(R.id.chat_btn);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeDriverActivity.this, ChatActivity.class);
                intent.putExtra("tripRequestId",tripRequestId);
                startActivity(intent);
            }
        });

        call_btn = layout.findViewById(R.id.call_btn);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("6972119950" ));
                startActivity(intent);
            }
        });

        cancel_btn = layout.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO alertbox or not? is tripid necessary for notification?
                sendCancelRequestNotification(tripRequestId);
                cancelTripRequest(tripRequestId);

            }
        });

        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // display the popup in the center
        pw.showAtLocation(mrelativeLayout, Gravity.BOTTOM, 0, 0);
    }


    private void cancelTripRequest(Long tripRequestId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.wtf("123", "apiservice"+apiService.toString());

        Call<TripRequest> call =  apiService.cancelRequest(tripRequestId);
        Log.wtf("123", "call"+call.toString());
        call.enqueue(new Callback<TripRequest>() {
            @Override
            public void onResponse(Call<TripRequest> call, Response<TripRequest> response) {
                Log.wtf("123", "response"+new Gson().toJson(response.body()));

                int msg =  response.code();
                Log.wtf("123", "message"+msg);

                if (response.isSuccessful()) {
                    Log.wtf("123", "response"+response.body().toString());
                    Log.wtf("123", "post submitted to API." + response.body().toString());
                    //TODO
                    //Cancel Route-Refresh activity-Subscribe in TOPIC
                    pw.dismiss();
                    handler.removeCallbacksAndMessages(null);
                    //TODO check for better solution not blink screen on refresh
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);

                }
            }

            @Override
            public void onFailure(Call<TripRequest> call, Throwable t) {
                Log.wtf("123", "Unable to submit post to API.");
            }
        });
    }

    // Send an Intent with an action named "cancel"
    private void sendCancelRequestNotification(Long tripRequestId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<Long> call = apiService.getDriverCancelRequestNotification(tripRequestId);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()){
                    Log.wtf("123", "Got notification:Driver cancel");
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });
    }



    private List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private void updateStatus(boolean status){
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("123", "apiservice" + apiService.toString());

        Call<Boolean> call = apiService.updateDriverStatus(status);
        Log.d("123", "call" + call.toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.wtf("123", "response" + new Gson().toJson(response.body()));

                int msg = response.code();
                Log.d("123", "message" + msg);

                if (response.isSuccessful()) {
                    Log.d("123", "response"+response.body().toString());
                    Log.d("123", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d("123", "Unable to submit post to API.");
                t.printStackTrace();
            }
        });

        Toast.makeText(WelcomeDriverActivity.this, "These are all your Credit Cards",
                Toast.LENGTH_SHORT).show();

    }


}
