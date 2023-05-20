package com.example.elephanttrackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FindLocation extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    private static final int GPS_TIME_INTERVAL = 1000 ; // get gps location
    private static final int GPS_DISTANCE = 1000;
    private static final int HANDLER_DELAY = 1000*60*5 ; //display the result
    private static final int START_HANDLER_DELAY = 0;


    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final static int PERMISSION_ALL = 1;

    //fire base
    EditText eleID;
    TextView F_latitude,F_longitude,F_Address,F_date;

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView longitude,latitude,address;
    Button upload;
    private final static int REQUEST_CODE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                requestLocation();
                handler.postDelayed(this, HANDLER_DELAY);
            }
        }, START_HANDLER_DELAY);

        setContentView(R.layout.activity_find_location);

        longitude=findViewById(R.id.longitude);
        latitude=findViewById(R.id.latitude);
        address=findViewById(R.id.address);
        upload=findViewById(R.id.btnUpload);
        eleID=findViewById(R.id.editTextElephantID);


        F_date=findViewById(R.id.DAT);

        Date date= Calendar.getInstance().getTime();
        F_date.setText(date.toString());

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadData();
            }
        });
    }
    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location!=null){
                                Geocoder geocoder=new Geocoder(FindLocation.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    latitude.setText(""+addresses.get(0).getLatitude());
                                    longitude.setText(""+addresses.get(0).getLongitude());
                                    address.setText(""+addresses.get(0).getAddressLine(0));
                                    F_latitude=latitude;
                                    F_longitude=longitude;
                                    F_Address=address;
                                    //uploadData();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }
        else{
            askPermission();
        }
    }
    private void askPermission(){
        ActivityCompat.requestPermissions(FindLocation.this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            requestLocation();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    handler.postDelayed(this, HANDLER_DELAY);
                }
            }, START_HANDLER_DELAY);
        } else {
            finish();
        }
    }
    //new
    public void onLocationChanged(@NonNull Location location) {
        Log.d("mylog", "Got Location: " + location.getLatitude() + ", " + location.getLongitude());
        getLastLocation();
        locationManager.removeUpdates(this);
    }
    //new
    private void requestLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        GPS_TIME_INTERVAL, GPS_DISTANCE, this);
            }
        }
    }


    //UPLOAD DATA
    public void uploadData(){
        String EleID=eleID.getText().toString();
        String latitude=F_latitude.getText().toString();
        String longitude=F_longitude.getText().toString();
        String address=F_Address.getText().toString();
        String DAT=F_date.getText().toString();

        LocationData ld=new LocationData(EleID,longitude,latitude,address,DAT);

        FirebaseDatabase.getInstance().getReference("Location Details").child(EleID+DAT)
                .setValue(ld).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(FindLocation.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FindLocation.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}