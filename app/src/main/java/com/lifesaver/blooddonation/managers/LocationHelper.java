package com.lifesaver.blooddonation.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.lifesaver.blooddonation.models.GeoLocation;

public class LocationHelper {

    public interface LocationCallback {
        void onLocation(@NonNull GeoLocation location);
        void onError(String message);
    }

    public static boolean hasFineLocationPermission(@NonNull Context ctx) {
        return ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void getCurrentLocation(@NonNull Context ctx,
                                          @NonNull LocationCallback cb) {
        if (!hasFineLocationPermission(ctx)) {
            cb.onError("Location permission not granted");
            return;
        }
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(ctx);
        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
                    new CancellationTokenSource().getToken())
                  .addOnSuccessListener(loc -> {
                      if (loc == null) { cb.onError("Location unavailable"); return; }
                      cb.onLocation(toGeo(loc));
                  })
                  .addOnFailureListener(e -> cb.onError(e.getMessage()));
        } catch (SecurityException e) {
            cb.onError(e.getMessage());
        }
    }

    private static GeoLocation toGeo(Location loc) {
        return new GeoLocation(loc.getLatitude(), loc.getLongitude(), null);
    }
}
