package com.example.stavroula.uber.service;

import com.example.stavroula.uber.entity.Car;
import com.example.stavroula.uber.entity.CreditCard;
import com.example.stavroula.uber.entity.Driver;
import com.example.stavroula.uber.entity.LoginData;
import com.example.stavroula.uber.entity.Photo;
import com.example.stavroula.uber.entity.Rating;
import com.example.stavroula.uber.entity.Rider;
import com.example.stavroula.uber.entity.Trip;
import com.example.stavroula.uber.entity.TripRequest;
import com.example.stavroula.uber.entity.TripRequestData;
import com.example.stavroula.uber.entity.TripStatistics;
import com.example.stavroula.uber.entity.User;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

        @GET("/users")
        Call<List<User>> getAllUsers();

        @POST("/users/rider/register")
        Call<User> createRider(@Body User user);

        @POST("/users/driver/register")
        Call<User> createDriver(@Body User user);

        @Headers("Content-Type: application/json")
        @POST("/users/rider/login")
        Call<User> loginUser(@Body LoginData body);

        @POST("/drivers/5/car")
        Call<Car> createCar(@Body Car car);

        @GET("/riders/5")
        Call<Rider> getRider();

        @GET("/drivers/13")
        Call<Rider> getDriver();

        @PUT("/users/user/4")
        Call<User> updateUser(@Body User user);

        @PUT("/users/user/4/newPassword")
        Call<User> updateUserPassword(@Body User user);

        @POST("/riders/9/creditCard")
        Call<CreditCard> createCreditCard(@Body CreditCard creditCard);

        @GET("/riders/9/creditCards")
        Call<List<CreditCard>> getCreditCards();

        @GET("/drivers/5/cars")
        Call<List<Car>> getCars();

        @POST("/drivers/15/trip/{tripId}/review")
        Call<Rating> saveRating(@Path("tripId") long id, @Body Rating rating);

        @GET("/riders/5/trips")
        Call<List<Trip>> getTrips();

        @GET("/riders/5/trips/9")
        Call<Trip> getTrip();

        @GET("/drivers/8/trips/statistics/{date}")
        Call<TripStatistics> getDayStatistics(@Path("date") String date);

        @GET("/requests/{tripRequestId}")
        Call<TripRequest> getTripRequest(@Path("tripRequestId") Long id);

        @POST("/drivers/7/acceptRequest/{tripRequestId}/{chatRoomId}")
        Call<TripRequest> acceptRequest(@Path("tripRequestId") Long id, @Path("chatRoomId") String chatroomId);

        @POST("/drivers/8/cancelRequest/{tripRequestId}")
        Call<TripRequest> cancelRequest(@Path("tripRequestId") Long id);

        @POST("/riders/5/cancelRequest/{tripRequestId}")
        Call<TripRequest> cancelTripRequest(@Path("tripRequestId") Long id);

        @GET("/maps/api/directions/json")
        Call<JsonObject> getDirections(@Query("mode") String mode,
                                       @Query("transit_routing_preference") String routingPreference,
                                       @Query("origin") String origin,
                                       @Query("destination") String destination,
                                       @Query("key") String apiKey);

        @GET("send/{tripRequestId}/")
        Call<Long> getNotification(@Path("tripRequestId") Long id);

        @GET("send/driver/{driverId}/{tripRequestId}/")
        Call<Long> getDriverDataNotification(@Path("driverId") Long driverId, @Path("tripRequestId") Long requestId);

        @GET("send/arrival/")
        Call<Long> getDriverArrivalNotification();

        @GET("/send/driverCancel/{tripRequestId}")
        Call<Long> getDriverCancelRequestNotification(@Path("tripRequestId") Long requestId);

        @GET("/send/riderCancel/{tripRequestId}")
        Call<Long> getRiderCancelRequestNotification(@Path("tripRequestId") Long requestId);

        @GET("/send/payment")
        Call<Long> getPaymentNotification();

        @POST("/riders/5/request")
        Call<TripRequest> createTripRequest(@Body TripRequestData tripRequestData);

        @POST("/drivers/8/acceptRequest/{tripRequestId}/startTrip")
        Call<Trip> startTrip(@Path("tripRequestId") Long id);

        @POST("/trips/{tripId}/payment/{paymentType}")
        Call<Trip> savePaymentType(@Path("paymentType") String type);

        @POST("/drivers/7/updateStatus/{isActive}")
        Call<Boolean> updateDriverStatus (@Path("isActive") boolean status);

        @POST("/drivers/7/updateLocation/{latitude}/{longitude}")
        Call<String> updateLocation (@Path("latitude") double latidute, @Path("longitude")double longitude);

        @Multipart
        @POST("/users/{userId}/photo/{1}")
        Call<Photo> uploadPhoto(@Part MultipartBody.Part photo);

        @GET("/drivers/activeDrivers/{status}")
        Call<List<Driver>> getActiveDrivers(@Path("status") boolean status);
    }
