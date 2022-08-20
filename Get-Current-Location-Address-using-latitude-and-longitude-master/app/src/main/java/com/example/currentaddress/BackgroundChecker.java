package com.example.currentaddress;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BackgroundChecker extends Service {

    private static final String TAG = "BackgroundChecker";
    private CountDownTimer countDownTimer;
    public static final String POSTS_API_URL = "https://gentle-depths-24532.herokuapp.com/?code=4&username=test&lat=200.54362324&lon=200.42343653";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 2000) {
            @Override
            public void onTick(long l) {
                Log.e(TAG, "onTick: " + l / 2000);



                getCurrentLocation();
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish: ");
            }
        }.start();
        //return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        Log.e(TAG, "Destroyed");
        super.onDestroy();
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(BackgroundChecker.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            String url = "https://gentle-depths-24532.herokuapp.com/?code=4&username=sasha&lat=" + lati + "&lon=" + longi;
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url(url).build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                }
                            });

                            Intent i = new Intent("location_update");
                            i.putExtra("coordinates", lati + " " +  longi);
                            sendBroadcast(i);
                            //textLatLong.setText(String.format("Latitude : %s\n Longitude: %s", lati, longi));

                            Location location = new Location("providerNA");
                            location.setLongitude(longi);
                            location.setLatitude(lati);
                            //fetchaddressfromlocation(location);

                        }
                    }
                }, Looper.getMainLooper());

    }
}
