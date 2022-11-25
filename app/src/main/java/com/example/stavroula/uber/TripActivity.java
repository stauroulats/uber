package com.example.stavroula.uber;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.stavroula.uber.entity.Trip;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TripActivity extends MainActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    private CameraPosition mCameraPosition;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Marker startPositionMarker, animationMarker;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final int DEFAULT_ZOOM = 15;

    private Location mLastKnownLocation;
    private ArrayList markerPoints= new ArrayList();

    private String KEY = "AIzaSyCBzOeULa1Vfi3qBuuwRVei7O8rqT_BLJI";

    private List<LatLng> polyLineList;

    private PolylineOptions polylineoptions, blackpolylineoptions;
    private Polyline blackpolyline, greypolyline;

    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;

    private float v;
    private double lat, lng;

    private GoogleMap mMap;

    private PopupWindow pw;

    private RelativeLayout mRelativeLayout;

    private final String TRIPTOPIC = "trip";

    public double charge = 0.74;

    Dialog dialog;

    String riderPosition, riderDestination, parsedDistance;
    Long tripId;

    TextView fare_amount_txt;
    Button end_trip_btn, credit_card_btn, cash_btn;
    LatLng tripStartLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trip_layout);

        // Get extra data included in the Intent
        Bundle bundle = getIntent().getExtras();
        riderPosition = bundle.getString("riderPosition");
        riderDestination = bundle.getString("riderDestination");
        tripId = bundle.getLong("tripId");

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.trip_map);
        mapFragment.getMapAsync(TripActivity.this);
        Log.wtf("123", "LOCATION" +mLastKnownLocation);
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

        createRoute(riderPosition, riderDestination);

    }

    private void createRoute(final String riderPosition, String riderDestination) {
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

        Call<JsonObject> call = apiService.getDirections("driving", "less_driving", riderPosition, riderDestination, KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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

                        JSONObject startLocation = leg.getJSONObject("start_location");
                        String startLocationLatitude = startLocation.getString("lat");
                        String startLocationLongitude = startLocation.getString("lng");
                        Log.wtf("123", "latlng" + startLocationLatitude+ " " + startLocationLongitude);
                        tripStartLocation = new LatLng(Double.valueOf(startLocationLatitude),Double.valueOf(startLocationLongitude));

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
                        parsedDistance = distance.getString("text");
                        Log.wtf("123", "distance"+parsedDistance);

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

                animationMarker = mMap.addMarker(new MarkerOptions().position(tripStartLocation)
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
                                float rotation = getBearing(startPosition, newPos);
                                if (animationMarker.getRotation() > rotation) {
                                    animationMarker.setRotation(animationMarker.getRotation() - valueAnimator.getAnimatedFraction());
                                } else {
                                    animationMarker.setRotation(animationMarker.getRotation() + valueAnimator.getAnimatedFraction());
                                }
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
                        if (index == (polyLineList.size() - 1)) {
                            valueAnimator.removeAllUpdateListeners();
                            Log.wtf("123", "end route");
                            handler.removeCallbacksAndMessages(null);
                            //Start Trip Button
                            dialog = new Dialog(TripActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.getWindow().setBackgroundDrawable(
                                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.setContentView(R.layout.end_trip_dialog_layout);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(true);
                            end_trip_btn = dialog.findViewById(R.id.end_trip_btn);

                            end_trip_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //TODO startTrip
                                    dialog.cancel();
                                    //Unsubscribe from Trip topic
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(TRIPTOPIC);

                                    initiateEndTripPopupWindow(tripId, parsedDistance);
                                    Intent intent = new Intent(TripActivity.this, DriverFareActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("tripId", tripId);
                                    intent.putExtra("parsedDistance", parsedDistance);
                                    //intent.putExtra("data1", );
                                    // intent.putExtra("data2", );
                                    startActivity(intent);
                                }
                            });

                            dialog.show();
                            //TODO popupwindow select payment-send notification to rider-on click rating both rider/driver
                            Intent broadcastIntent = new Intent("endTripNotification");
                            broadcastIntent.putExtra("tripId", tripId);
                            LocalBroadcastManager.getInstance(TripActivity.this).sendBroadcast(broadcastIntent);
                        }
                    }
                }, 3000);

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

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
                locationResult.addOnCompleteListener(TripActivity.this, new OnCompleteListener<Location>() {
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
                            /*Marker car icon*/
                            startPositionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

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

    public LatLng getLocationFromAddress(Context context, String strAddress) {

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

    private void initiateEndTripPopupWindow(final Long tripId, String parsedDistance) {

        LayoutInflater inflater = LayoutInflater.from(this);
        //Inflate the view from a predefined XML layout
        View layout = inflater.inflate(R.layout.pop_up_end_trip_layout,null);

        mRelativeLayout = layout.findViewById(R.id.pop_up_window);
        // create a PopupWindow
        pw = new PopupWindow(this);
        pw.setContentView(layout);

        String[] splited = parsedDistance.split("\\s+");
        final Double distance_value = Double.parseDouble(splited[0]);
        Log.wtf("123", "split"+splited[0]);
        Log.wtf("123", "charge"+charge);
        double total = (distance_value*charge);
        Log.wtf("123", "total!"+total);
        DecimalFormat df = new DecimalFormat("0.00");

        fare_amount_txt = layout.findViewById(R.id.fare_amount_value);
        fare_amount_txt.setText(String.valueOf(df.format(total))+"$");


        credit_card_btn = layout.findViewById(R.id.call_btn);
        credit_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO sendNotification - end trip request
                String creditCard = "creditCard";
                savePayment(creditCard);

            }
        });

        cash_btn = layout.findViewById(R.id.cancel_btn);
        cash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("123", "TRIPID"+tripId);
                //TODO notification - end trip request
                String cash = "cash";
                savePayment(cash);
            }
        });

        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // display the popup in the center
        pw.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
    }

    private void savePayment(final String type) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<Trip> call = apiService.savePaymentType(type);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful()){
                    Log.wtf("123", "Payment Method"+type);
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {

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
