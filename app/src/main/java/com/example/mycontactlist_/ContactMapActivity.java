package com.example.mycontactlist_;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity {


    Location currentBestLocation;
    LocationManager locationManager;
    LocationListener gpsListener;

    LocationListener networkListener;
    final int PERMISSION_REQUEST_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    //Listing 7.5 Code for startLocationUpdate
    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        //Listing 7.3 code moved here
        try {
            locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            gpsListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    TextView txtLatitude = (TextView) findViewById(R.id.textLatitude);
                    TextView txtLongitude = (TextView) findViewById(R.id.textLongitude);
                    TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);

                    if (isBetterLocation(location)) {
                        currentBestLocation = location;

                        txtLatitude.setText(String.valueOf(currentBestLocation.getLatitude()));
                        txtLongitude.setText(String.valueOf(currentBestLocation.getLongitude()));
                        txtAccuracy.setText(String.valueOf(currentBestLocation.getAccuracy()));
                    }
                }
                public void onStatusChanged(String provider, int status, Bundle extras){}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider){}
            };

            networkListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    TextView txtLatitude = (TextView) findViewById(R.id.textLatitude);
                    TextView txtLongitude = (TextView) findViewById(R.id.textLongitude);
                    TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);

                    if (isBetterLocation(location)) {

                        currentBestLocation = location;

                        txtLatitude.setText(String.valueOf(currentBestLocation.getLatitude()));
                        txtLongitude.setText(String.valueOf(currentBestLocation.getLongitude()));
                        txtAccuracy.setText(String.valueOf(currentBestLocation.getAccuracy()));
                    }
                }
                public void onStatusChanged(String provider, int status, Bundle extras){}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider){}
            };


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error, Location not available", Toast.LENGTH_LONG).show();

        }
    }

    // Listing 7.7 Method to respond to permission requests
    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else  {
                    Toast.makeText(ContactMapActivity.this, "MyContactList will not locate your contacts",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //Listing 7.4 onPause Method
    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        try {
            locationManager.removeUpdates(networkListener);
            locationManager.removeUpdates(gpsListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Listing 7.1 Code to look up address coords(Listing 7.3 moved from onClick Method)
    //listing 7.6 code moved into OnClick method
    private void initGetLocationButton() {
        Button locationButton = (Button) findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    ContactMapActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            )) {
                                Snackbar.make(findViewById(R.id.activity_contact_map),
                                                "MyContactList requires this permission to locate " +
                                                        "your contacts", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ActivityCompat.requestPermissions(
                                                        ContactMapActivity.this,
                                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                        PERMISSION_REQUEST_LOCATION
                                                );

                                            }
                                        })
                                        .show();

                            } else {
                                ActivityCompat.requestPermissions(
                                        ContactMapActivity.this,
                                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        } else {
                            startLocationUpdates();
                        }
                    } else {
                        startLocationUpdates();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Error requesting permission",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Listing 7.8  isBetterLocation Method
    private boolean isBetterLocation(Location location) {
        boolean isBetter = false;
        if (currentBestLocation == null) {
            isBetter = true;
        } else if (location.getAccuracy() <= currentBestLocation.getAccuracy()) {
            isBetter = true;
        } else if (location.getTime() - currentBestLocation.getTime() > 5 * 60 * 1000) {
            isBetter = true;
        }
        return isBetter;
    }

}