package com.example.stavroula.uber.service;

import com.example.stavroula.uber.entity.Car;
import com.example.stavroula.uber.entity.CreditCard;
import com.example.stavroula.uber.entity.LoginData;
import com.example.stavroula.uber.entity.Rating;
import com.example.stavroula.uber.entity.Rider;
import com.example.stavroula.uber.entity.Trip;
import com.example.stavroula.uber.entity.TripRequest;
import com.example.stavroula.uber.entity.TripRequestData;
import com.example.stavroula.uber.entity.TripStatistics;
import com.example.stavroula.uber.entity.User;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

        @POST("/drivers/15/car")
        Call<Car> createCar(@Body Car car);

        @GET("/riders/5")
        Call<Rider> getRider();

        @PUT("/users/user/4")
        Call<User> updateUser(@Body User user);

        @PUT("/users/user/4/newPassword")
        Call<User> updateUserPassword(@Body User user);

        @POST("/riders/5/creditCard")
        Call<CreditCard> createCreditCard(@Body CreditCard creditCard);

        @GET("/riders/5/creditCard")
        Call<List<CreditCard>> getCreditCards();

        @GET("/drivers/8/cars")
        Call<List<Car>> getCars();

        @POST("/drivers/15/trip/7/review")
        Call<Rating> saveRating(@Body Rating rating);

        @GET("/riders/5/trips")
        Call<List<Trip>> getTrips();

        @GET("/riders/5/trips/9")
        Call<Trip> getTrip();

        @GET("/drivers/8/trips/statistics/{date}")
        Call<TripStatistics> getDayStatistics(@Path("date") String date);

        @GET("/requests/{tripRequestId}")
        Call<TripRequest> getTripRequest(@Path("tripRequestId") Long id);

        @POST("/drivers/8/acceptRequest/{tripRequestId}")
        Call<TripRequest> acceptRequest(@Path("tripRequestId") Long id);

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

        @POST("/riders/5/request")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        Call<TripRequest> createTripRequest(@Body TripRequestData tripRequestData);


        /*@Multipart
        @POST("upload-image")
        Call<Photo> uploadFile(@Part MultipartBody.Part file, @Part("dishes_name") RequestBody name);*/
    }
