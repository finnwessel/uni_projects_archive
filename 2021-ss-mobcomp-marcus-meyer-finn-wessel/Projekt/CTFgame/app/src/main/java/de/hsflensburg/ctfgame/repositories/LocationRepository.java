package de.hsflensburg.ctfgame.repositories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

public class LocationRepository implements LocationListener {

    private final Context context;

    public MutableLiveData<Location> location;

    public LocationRepository(Context c) {
        context = c;
        location = new MutableLiveData<>();
    }

    public void startLocationListener() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, this);
            } else {
                Toast.makeText(context, "Please enable GPS", Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location.postValue(location);
    }

    @Override
    public void onStatusChanged (String provider, int status, Bundle extras) {
        Log.d("LocationRepository", "Status changed");
    }
}
