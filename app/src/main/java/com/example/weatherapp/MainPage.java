package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    String API_KEY;
    Location LOCATION;
    ArrayList<String> coordinates;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coordinates = new ArrayList<>();
        LOCATION = null;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_main_page);
        API_KEY = getIntent().getStringExtra("API_KEY");
        Log.d("UGUR",API_KEY);
    }

    public void getCurrentWeatherForecast(View view){
        getLocationPermission();
    }

    public void getLocationPermission(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            makeLocationRequest();
                            buildLocationCallback();
                        }
                        if(ActivityCompat.checkSelfPermission(MainPage.this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                        //fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(MainPage.this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void makeLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    private void buildLocationCallback(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);

                LOCATION = locationResult.getLastLocation();
                Log.d("UGUR",String.valueOf(LOCATION.getLatitude()));
                coordinates.add(String.valueOf(LOCATION.getLatitude()));
                coordinates.add(String.valueOf(LOCATION.getLongitude()));
                Intent intent = new Intent(getBaseContext(),CurrentWeather.class);
                Bundle extras = new Bundle();
                extras.putString("LAT",String.valueOf(LOCATION.getLatitude()));
                extras.putString("LONG",String.valueOf(LOCATION.getLongitude()));
                extras.putString("API_KEY",API_KEY);
                intent.putExtras(extras);
                startActivity(intent);

            }
        };
    }

}
