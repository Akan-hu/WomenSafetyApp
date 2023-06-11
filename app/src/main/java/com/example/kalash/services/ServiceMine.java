package com.example.kalash.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.kalash.R;
import com.example.kalash.ui.MainActivity;
import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ServiceMine extends Service {

    boolean isRunning = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static final String PHONE_NUMBER = "phoneNumber";
    static final String SHARED_PREF_NAME = "WomenSafetyApp";
    String message="I'm in Trouble!!";
    static final String SMS_MESSAGE="sms_message";
    SmsManager manager = SmsManager.getDefault();
    String myLocation;
    Location location1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getLocation();
        // Log the location
        if (location1 != null) {
            Log.d("LAST LOCATION", "Latitude: " + location1.getLatitude() + ", Longitude: " + location1.getLongitude());

              myLocation = "http://maps.google.com/maps?q=loc:" + location1.getLatitude() + "," + location1.getLongitude();
        } else {
              myLocation = "Unable to Find Location :(";
        }


        ShakeDetector.create(this, () -> {
            for (String s : getPhoneNumber())
//                manager.sendTextMessage(s, null, getSOSMessage() + "\nSending My Location :\n" + myLocation, null, null);
                Log.d("LAST LOCATION", "Latitude: " + location1.getLatitude() + ", Longitude: " + location1.getLongitude());

                }
        );

    }



    private void getLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        location1 = location;
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                       location1=location;
                    }
                });
    }


    public Set<String> getPhoneNumber() {
        Set<String> mutableSet = new HashSet<String>();
        return sharedPreferences.getStringSet(PHONE_NUMBER, mutableSet);
    }
    public String getSOSMessage() {
        return sharedPreferences.getString(SMS_MESSAGE, message);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            if (intent.getAction().equalsIgnoreCase("STOP")) {
                if(isRunning) {
                    this.stopForeground(true);
                    this.stopSelf();
                }
            } else {


                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("MYID", "CHANNELFOREGROUND", NotificationManager.IMPORTANCE_DEFAULT);

                    NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    m.createNotificationChannel(channel);

                    Notification notification = new Notification.Builder(this, "MYID")
                            .setContentTitle("Women Safety")
                            .setContentText("Shake Device to Send SOS")
                            .setSmallIcon(R.drawable.girl_vector)
                            .setContentIntent(pendingIntent)
                            .build();
                    this.startForeground(115, notification);
                    isRunning = true;
                    return START_NOT_STICKY;
                }
            }

        return super.onStartCommand(intent,flags,startId);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
