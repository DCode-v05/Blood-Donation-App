package com.lifesaver.blooddonation.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.constants.AppConstants;
import com.lifesaver.blooddonation.managers.FirestoreManager;
import com.lifesaver.blooddonation.models.BloodCamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private final Map<String, BloodCamp> markerCamp = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        tb.setNavigationOnClickListener(v -> finish());

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnInfoWindowClickListener(this::openCamp);
        loadCamps();
    }

    private void loadCamps() {
        FirestoreManager.get().fetchActiveCamps(new FirestoreManager.ListCallback<BloodCamp>() {
            @Override public void onLoaded(List<BloodCamp> items) { plot(items); }
            @Override public void onError(String message) { /* silent */ }
        });
    }

    private void plot(List<BloodCamp> camps) {
        if (map == null || camps.isEmpty()) return;
        LatLng first = null;
        for (BloodCamp c : camps) {
            if (c.getLocation() == null) continue;
            LatLng pos = new LatLng(c.getLocation().getLatitude(),
                                    c.getLocation().getLongitude());
            Marker m = map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(c.getName())
                    .snippet(c.getAddress()));
            if (m != null) markerCamp.put(m.getId(), c);
            if (first == null) first = pos;
        }
        if (first != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 11f));
        }
    }

    private void openCamp(Marker marker) {
        BloodCamp c = markerCamp.get(marker.getId());
        if (c == null) return;
        Intent i = new Intent(this, BloodCampDetailActivity.class);
        i.putExtra(AppConstants.EXTRA_CAMP, c);
        startActivity(i);
    }
}
