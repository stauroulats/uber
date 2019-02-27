package com.example.stavroula.uber;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stavroula.uber.entity.TripRequest;
import com.example.stavroula.uber.entity.TripRequestData;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RiderMapActivity extends AppCompatActivity
        implements OnMapReadyCallback,GoogleMap.OnMyLocationClickListener {

    private static final String TAG = RiderMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    PlaceAutocompleteFragment placeAutoComplete;
    private Marker previousMarker, animationMarker;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private ArrayList markerPoints= new ArrayList();

    private ImageButton appointment_btn , close_btn;

    private PopupWindow pw;

    private RelativeLayout mRelativeLayout;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Place gplace;

    private String KEY = "AIzaSyB_JRrGrBbcMHGkzl79HkE8HDOIUJ-JmXA";

    private List<LatLng> polyLineList;

    public String parsedDistance;

    private PolylineOptions polylineoptions, blackpolylineoptions;
    private Polyline blackpolyline, greypolyline;

    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;

    private float v;
    private double lat, lng;

    public double charge = 0.74;

    private final String TOPIC = "tripRequest";
    private final String TRIPTOPIC = "trip";

    RelativeLayout relativeLayout;
    ImageView driver_img, car_img;
    TextView name_txt, car_details_txt, registration_plate_txt, rating_txt;
    Button call_btn, message_btn, cancel_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.second_activity);

        //TODO fix the notifications
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);

        FirebaseMessaging.getInstance().subscribeToTopic(TRIPTOPIC);


        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("acceptTripRequestNotification"));

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("driver-arrival"));

        LocalBroadcastManager.getInstance(this).registerReceiver(cancelMessageReceiver,
                new IntentFilter("cancelTripRequestNotification"));


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);

        //Filter for address & country
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setCountry("GR")
                .build();
        placeAutoComplete.setFilter(typeFilter);
        placeAutoComplete.setHint("Where to?");

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                gplace = place;

                Log.wtf("Maps", "Place gplace: " + gplace.getAddress());
                Log.wtf("Maps", "Place g: " + place.getLatLng());
                if (previousMarker != null) {
                    previousMarker.remove();
                }
                Log.wtf("Maps", "Place selected: " + place.getName());
                addMarker(place);
                markerPoints.add(place.getLatLng());

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng start = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);
                    Log.wtf("123", "latlng start = " + start + "latlng dest is = " + dest);
                }

                final LatLng riderPosiiton = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                final String origin =getCompleteAddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                final String destination= gplace.getAddress().toString();
                Log.wtf("123", "origin,destination" + origin + destination);
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

                ApiService apiService = retrofit.create(ApiService.class);
                Log.wtf("123", "apiservice"+apiService.toString());

                Call<JsonObject> call =  apiService.getDirections("driving","less_driving",origin,destination,KEY);
                Log.wtf("123", "call"+call.request().toString());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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
                                JSONObject steps = legs.getJSONObject(0);
                                JSONObject distance = steps.getJSONObject("distance");
                                parsedDistance = distance.getString("text");
                                Log.wtf("123", "distance"+parsedDistance);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
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

                        Log.wtf("123", "Unable to submit post to API."+parsedDistance);
                        initiatePopupWindow(origin,destination,parsedDistance);

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
                                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                valueAnimator.setDuration(3000);
                                valueAnimator.setInterpolator(new LinearInterpolator());
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
                                        mMap.moveCamera(CameraUpdateFactory
                                                .newCameraPosition
                                                        (new CameraPosition.Builder()
                                                                .target(newPos)
                                                                .zoom(15.5f)
                                                                .build()));
                                    }
                                });
                              //valueAnimator.start();
                                handler.postDelayed(this, 3000);
                            }
                        }, 3000);

                    }


                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.wtf("123", "Unable to submit post to API.");
                    }

                });

            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.wtf("Maps", "Build map" +
                "");


        //Clock button/Schedule Trip
       appointment_btn =  findViewById(R.id.appointment_icon);
       appointment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip_schedule();
            }
        });


        //Navigation Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open_nav_menu,R.string.close_nav_menu);
        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.syncState();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();

        actionbar.setTitle(0);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_payment:
                        Toast.makeText(RiderMapActivity.this, "Payment", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(RiderMapActivity.this, PaymentActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_history:
                        Toast.makeText(RiderMapActivity.this, "History", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(RiderMapActivity.this, TripHistoryActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_profile:
                        Toast.makeText(RiderMapActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(RiderMapActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_log_out:
                        AlertDialog.Builder builder=new AlertDialog.Builder(RiderMapActivity.this);
                        builder.setMessage("Do you want to exit?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO check if there is a better solution
                                finishAffinity();
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

                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                mDrawerLayout.bringToFront();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

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

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                Log.wtf(TAG, "Current location" + locationResult.toString());
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            /*Marker car icon*/
                            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                            // Adding new item to the ArrayList
                            markerPoints.add(latLng);



                        } else {
                            Log.wtf(TAG, "Current location is null. Using defaults.");
                            Log.wtf(TAG, "Exception: %s", task.getException());
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
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        String address = getCompleteAddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        Toast.makeText(this, "Current location:\n" + address, Toast.LENGTH_LONG).show();
    }


    /**
     * Prompts the user for permission to use the device location.
     */
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
            Log.wtf(TAG, "Permission Granted");
        } else {// Build the map.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.wtf(TAG, "Permission not Granted");
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

    private void trip_schedule() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_appointment = inflater.inflate(R.layout.layout_schedule_appointment,null);
        Intent intent = new Intent(RiderMapActivity.this,TripSchedule.class);
        startActivity(intent);
    }

    private void addMarker(Place p){

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(p.getLatLng());
        markerOptions.title(p.getName()+"");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        previousMarker =  mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location("someLoc");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    private float bearingBetweenLatLngs(LatLng beginLatLng,LatLng endLatLng) {
        Location beginLocation = convertLatLngToLocation(beginLatLng);
        Location endLocation = convertLatLngToLocation(endLatLng);
        return beginLocation.bearingTo(endLocation);
    }

    public void initiatePopupWindow(final String pickUpAddress, final String destination, final String distance) {
        try {

            LayoutInflater inflater = LayoutInflater.from(this);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.pop_up_rider_map,null);


            mRelativeLayout = layout.findViewById(R.id.pop_up_window);
            // create a PopupWindow
            pw = new PopupWindow(this);
            pw.setContentView(layout);
            // display the popup in the center
            pw.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);


            TextView mResultText = layout.findViewById(R.id.text);
            TextView estimated_fare = layout.findViewById(R.id.estimated_fare_txt);
            Log.wtf("123", "Canont get Address!"+distance);
            String[] splited = distance.split("\\s+");
            final Double distance_value = Double.parseDouble(splited[0]);
            Log.wtf("123", "split"+splited[0]);
            Log.wtf("123", "charge"+charge);
            double total = (distance_value*charge);
            Log.wtf("123", "total!"+total);
            DecimalFormat df = new DecimalFormat("0.00");
            estimated_fare.setText(String.valueOf(df.format(total))+"$");
            Log.wtf("123", "Canont get Address!"+total);

            Button request_uber_btn = layout.findViewById(R.id.request_uber_btn);

           /* final TripRequestData tripRequestData = new TripRequestData();
            tripRequestData.setPickUpPoint(pickUpAddress);
            tripRequestData.setDestination(destination);
            tripRequestData.setRideDistance(distance_value);*/

            request_uber_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                    Log.wtf("123", "apiservice"+apiService.toString());

                    Call<TripRequest> call =  apiService.createTripRequest(new TripRequestData(pickUpAddress, destination, distance_value));
                    Log.wtf("123", "call"+call.request().toString());
                    call.enqueue(new Callback<TripRequest>() {
                        @Override
                        public void onResponse(Call<TripRequest> call, Response<TripRequest> response) {
                            Log.wtf("123", "response"+new Gson().toJson(response.body()));
                            int msg =  response.code();
                            Log.wtf("123", "message"+msg);
                            if (response.isSuccessful()) {
                                Long tripRequest_id = response.body().getId();
                                Log.wtf("123", "tripid"+tripRequest_id);
                                Call<Long> call2 = apiService.getNotification(tripRequest_id);
                                call2.enqueue(new Callback<Long>() {
                                    @Override
                                    public void onResponse(Call<Long> call, Response<Long> response) {
                                    }

                                    @Override
                                    public void onFailure(Call<Long> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<TripRequest> call, Throwable t) {
                            Log.wtf("123", "Unable to submit post to API.");
                        }
                    });
                    pw.dismiss();
                }
            });

            close_btn = findViewById(R.id.close_btn);

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

    //Broadcast "acceptNotification"
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Subscribe to trip TOPIC
            FirebaseMessaging.getInstance().subscribeToTopic(TRIPTOPIC);

            // Get extra data included in the Intent
            Bundle bundle = intent.getExtras();
            Long tripRequestId = Long.valueOf(bundle.getString("data1"));
            String firstName = bundle.getString("data2");
            String lastName = bundle.getString("data3");
            String manufacturer = bundle.getString("data4");
            String model = bundle.getString("data5");
            String registrationPlate = bundle.getString("data6");
            Log.wtf("firstName", "Got message: " + firstName);
            Log.wtf("tripRequestId", "Got message: " + tripRequestId);
            Toast.makeText(RiderMapActivity.this,"I receive the notification",Toast.LENGTH_LONG).show();
            initiateDriverInfoPopupWindow(firstName, lastName, manufacturer, model, registrationPlate, tripRequestId);
        }
    };

    //Broadcast "driver-arrival"
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            Bundle bundle = intent.getExtras();
            String body =  bundle.getString("data1");

            //Alert box to inform rider that the driver has arrived
            new AlertDialog.Builder(RiderMapActivity.this)
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

    //Broadcast "cancelTripRequestNotification"
    private BroadcastReceiver cancelMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Unsubscribe from trip TOPIC
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TRIPTOPIC);

            // Get extra data included in the Intent
            Bundle bundle = intent.getExtras();
            String body =  bundle.getString("data1");

            //Alert box to inform rider that the driver has arrived
            new AlertDialog.Builder(RiderMapActivity.this)
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    private void initiateDriverInfoPopupWindow(String firstName, String lastName, String manufacturer, String model, String registartionPlate, final Long tripRequestId) {
        //Invisibile AutoCompleteView search tab
        placeAutoComplete.getView().setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        //Inflate the view from a predefined XML layout
        View layout = inflater.inflate(R.layout.pop_up_trip_rider,null);

        relativeLayout = layout.findViewById(R.id.pop_up_window);
        // create a PopupWindow
        pw = new PopupWindow(this);
        pw.setContentView(layout);

        name_txt = layout.findViewById(R.id.name);
        car_details_txt = layout.findViewById(R.id.car_details);
        name_txt.setText(firstName + " " + lastName);
        car_details_txt.setText(manufacturer+" "+model);

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
                Log.wtf("123", "TRIPREQUESTID"+tripRequestId);
                //TODO alertbox or not? is tripid necessary for notification?
                sendCancelRequestNotification(tripRequestId);
                cancelTripRequest(tripRequestId);
            }
        });

        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // display the popup in the center
        pw.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);
    }

    private void cancelTripRequest(Long tripRequestId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.wtf("123", "apiservice"+apiService.toString());

        Call<TripRequest> call =  apiService.cancelTripRequest(tripRequestId);
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
                    //TODO check if its ok
                    pw.dismiss();
                    //TODO check for better solution not blink screen on refresh

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

        Call<Long> call = apiService.getRiderCancelRequestNotification(tripRequestId);
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

}
