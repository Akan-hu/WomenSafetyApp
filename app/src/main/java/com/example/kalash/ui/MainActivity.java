package com.example.kalash.ui;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kalash.R;
import com.example.kalash.services.ServiceMine;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static final String PHONE_NUMBER = "phoneNumber";
    static final String IS_LOGIN = "is_login";
    static final String SHARED_PREF_NAME = "WomenSafetyApp";
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private final int REQUEST_LOCATION = 199;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MYID", "CHANNELFOREGROUND", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            m.createNotificationChannel(channel);
        }
    }


    private final ActivityResultLauncher<String[]> multiplePermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {

            for (Map.Entry<String,Boolean> entry : result.entrySet())
                if(!entry.getValue()){
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Permission Must Be Granted!", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            multiplePermissions.launch(new String[]{entry.getKey()});
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
        }

    });


    public void stopService(View view) {

        Intent notificationIntent = new Intent(this, ServiceMine.class);
        notificationIntent.setAction("stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(notificationIntent);
            Snackbar.make(findViewById(android.R.id.content),"Service Stopped!", Snackbar.LENGTH_LONG).show();
            findViewById(R.id.start).setVisibility(View.VISIBLE);
            findViewById(R.id.stop).setVisibility(View.GONE);
        }
    }

    public void startServiceV(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  ) {

            if (!checkGPSStatus()) return;

            Intent notificationIntent = new Intent(this,ServiceMine.class);
            notificationIntent.setAction("Start");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(notificationIntent);
                Snackbar.make(findViewById(android.R.id.content),"Service Started!", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.start).setVisibility(View.GONE);
                findViewById(R.id.stop).setVisibility(View.VISIBLE);
            }
        }else{
            multiplePermissions.launch(new String[]{Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION});
        }


    }

    public boolean checkGPSStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
            return false;
        } else {
            return true;
        }
    }

    public void enableLoc() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(connectionResult ->
                        Log.d("Location error", "Location error " + connectionResult.getErrorCode())
                )
                .build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result;
        result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(this, REQUEST_LOCATION);
                        // finish()
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
            }
        });
    }


    public void PopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);
        popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.changeNum){
                    startActivity(new Intent(MainActivity.this,ContactPickerActivity.class));
                    finish();
                }
                if(item.getItemId() == R.id.sign_out){
                    FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    Toast.makeText(MainActivity.this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,IntroActivity.class));
                    Set<String> set = new HashSet<>();
                    putPhoneNumber(set);

                  putLogin(false);
                }

                return true;
            }
        });
        popupMenu.show();
    }

    public void putLogin(boolean isLogin ) {
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.apply();
    }
    public void putPhoneNumber( Set<String> number) {
        editor.putStringSet(PHONE_NUMBER, number);
        editor.apply();
    }

}