package com.example.stavroula.uber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.stavroula.uber.entity.TripRequest;
import com.example.stavroula.uber.entity.TripRequestData;
import com.example.stavroula.uber.network.RetrofitClient;
import com.example.stavroula.uber.service.ApiService;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripSchedule extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationClickListener {

    ImageButton return_back_btn, close_btn;
    Button pickUp_date_btn, pickUp_time_btn;
    TimePicker timePicker;
    Calendar calendar;
    TextView pick_up_point;
    private GoogleMap mMap;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private static final String TAG = RiderMapActivity.class.getSimpleName();
    private CameraPosition mCameraPosition;
    AutocompleteSupportFragment placeAutoComplete;
    private Marker previousMarker;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    private String KEY = "AIzaSyB_JRrGrBbcMHGkzl79HkE8HDOIUJ-JmXA";

    private List<LatLng> polyLineList;

    public String parsedDistance;

    private PolylineOptions polylineoptions, blackpolylineoptions;
    private Polyline blackpolyline, greypolyline;

    private PopupWindow pw;
    RelativeLayout mRelativeLayout;

    public double charge = 0.74;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_schedule_appointment);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.appointmentMap);
        mapFragment.getMapAsync(TripSchedule.this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        return_back_btn = findViewById(R.id.return_back_btn);
        return_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        calendar = Calendar.getInstance();

        pickUp_time_btn = findViewById(R.id.pickUp_time_btn);

        pickUp_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append("You select time is ");
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);

                       /* TextView timePickerValueTextView = (TextView) findViewById(R.id.timePickerValue);
                        timePickerValueTextView.setText(strBuf.toString());*/

                        Toast.makeText(TripSchedule.this,strBuf.toString(), Toast.LENGTH_LONG).show();

                        pickUp_time_btn.setText(String.format("%02d:%02d", hour, minute));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(TripSchedule.this, onTimeSetListener, hour, minutes, false);
                timePickerDialog.setTitle("Please select time");
                timePickerDialog.show();
            }

        });

        pickUp_date_btn = findViewById(R.id.pickUp_date_btn);

        pickUp_date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get current year, month and day.
                int year =calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append("You select date is ");
                        strBuf.append(year);
                        strBuf.append("-");
                        strBuf.append(month+1);
                        strBuf.append("-");
                        strBuf.append(day);

                        Toast.makeText(TripSchedule.this,strBuf.toString(), Toast.LENGTH_LONG).show();

                        pickUp_date_btn.setText((day + "-" + (month + 1 ) + "-" + year));
                    }
                };

                // Create the new DatePickerDialog instance.
                DatePickerDialog datePickerDialog = new DatePickerDialog(TripSchedule.this, onDateSetListener, year, month, day);

                // Set dialog icon and title.
                datePickerDialog.setIcon(R.drawable.car);
                datePickerDialog.setTitle("Please select date.");

                // Popup the dialog.
                datePickerDialog.show();
            }
        });


        placeAutoComplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destination_place_autocomplete);

        //Filter for address & country
       /*AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setCountry("GR")
                .build();
        placeAutoComplete.setFilter(typeFilter);
        placeAutoComplete.setHint("Enter your destination");
        placeAutoComplete.getView().setTextAlignment(TEXT_ALIGNMENT_CENTER);*/
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng string_location = place.getLatLng();
                String address = (String) place.getAddress();
                String name = (String) place.getName();
                Log.wtf("My Current loction address",string_location+address+name);
               addMarker(place);


                final LatLng riderPosiiton = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                final String origin =getCompleteAddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                final String destination= place.getAddress().toString();
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
                                Log.wtf("123", "distance" + parsedDistance);

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
                            initiatePopupWindow(origin,destination,parsedDistance);
                        }
                    }
                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.wtf("123", "Unable to submit post to API.");
                        }

                    });
                }

            @Override
            public void onError(Status status) {
            }
        });
        }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

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
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            /*Marker car icon*/
                            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            //Set the name of current location as a pick up point
                            String address = getCompleteAddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            pick_up_point = findViewById(R.id.pickUp_location_txt);
                            pick_up_point.setText(address);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
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
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

    private void addMarker(Place p){

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(p.getLatLng());
        markerOptions.title(p.getName()+"");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        previousMarker =  mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void initiatePopupWindow(final String pickUpAddress,final String destination, String distance) {

        LayoutInflater inflater = LayoutInflater.from(this);
        //Inflate the view from a predefined XML layout
        View layout = inflater.inflate(R.layout.pop_up_appointment,null);


        mRelativeLayout = layout.findViewById(R.id.pop_up_window);
        // create a PopupWindow
        pw = new PopupWindow(this);
        pw.setContentView(layout);
        // display the popup in the center
        pw.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);


        TextView mResultText = layout.findViewById(R.id.text);
        TextView estimated_fare = layout.findViewById(R.id.estimated_fare_txt);
        Log.wtf("123", "Distance!"+distance);
        String[] splited = distance.split("\\s+");
        final Double distance_value = Double.parseDouble(splited[0]);
        Log.wtf("123", "split"+splited[0]);
        Log.wtf("123", "charge"+charge);
        double total = (distance_value*charge);
        Log.wtf("123", "total!"+total);
        DecimalFormat df = new DecimalFormat("0.00");
        estimated_fare.setText(String.valueOf(df.format(total))+"$");
        Log.wtf("123", "Canont get Address!"+total);

        Button request_uber_btn = layout.findViewById(R.id.schedule_btn);

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

/*        // dismiss the popup window when touched
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pw.dismiss();
                return true;
            }
        });*/
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
}
