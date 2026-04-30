package com.lifesaver.blooddonation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.activities.BloodCampDetailActivity;
import com.lifesaver.blooddonation.activities.BloodRequestDetailActivity;
import com.lifesaver.blooddonation.activities.MapViewActivity;
import com.lifesaver.blooddonation.adapters.BloodCampAdapter;
import com.lifesaver.blooddonation.adapters.BloodRequestAdapter;
import com.lifesaver.blooddonation.constants.AppConstants;
import com.lifesaver.blooddonation.managers.FirestoreManager;
import com.lifesaver.blooddonation.managers.LocationHelper;
import com.lifesaver.blooddonation.models.BloodCamp;
import com.lifesaver.blooddonation.models.BloodRequest;
import com.lifesaver.blooddonation.models.GeoLocation;
import com.lifesaver.blooddonation.utils.DistanceUtils;

import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private BloodCampAdapter    campsAdapter;
    private BloodRequestAdapter requestsAdapter;
    private TextView empty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        empty = v.findViewById(R.id.text_empty);

        RecyclerView campsRv = v.findViewById(R.id.recycler_camps);
        campsAdapter = new BloodCampAdapter(this::openCamp);
        campsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        campsRv.setAdapter(campsAdapter);

        RecyclerView requestsRv = v.findViewById(R.id.recycler_requests);
        requestsAdapter = new BloodRequestAdapter(this::openRequest);
        requestsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        requestsRv.setAdapter(requestsAdapter);

        ((MaterialButton) v.findViewById(R.id.button_open_map))
                .setOnClickListener(b -> startActivity(
                        new Intent(getActivity(), MapViewActivity.class)));

        loadCamps();
        loadRequests();
    }

    private void loadCamps() {
        FirestoreManager.get().fetchActiveCamps(new FirestoreManager.ListCallback<BloodCamp>() {
            @Override public void onLoaded(List<BloodCamp> items) {
                computeDistancesThenSubmit(items);
            }
            @Override public void onError(String message) {
                campsAdapter.submit(Collections.emptyList());
                checkEmpty();
            }
        });
    }

    private void computeDistancesThenSubmit(List<BloodCamp> camps) {
        if (getContext() == null) return;
        if (!LocationHelper.hasFineLocationPermission(getContext())) {
            campsAdapter.submit(camps);
            checkEmpty();
            return;
        }
        LocationHelper.getCurrentLocation(getContext(),
                new LocationHelper.LocationCallback() {
                    @Override public void onLocation(@NonNull GeoLocation me) {
                        for (BloodCamp c : camps) {
                            if (c.getLocation() == null) continue;
                            c.setDistanceKm(DistanceUtils.calculate(me, c.getLocation()));
                        }
                        Collections.sort(camps, (a, b) -> Double.compare(
                                a.getDistanceKm() < 0 ? Double.MAX_VALUE : a.getDistanceKm(),
                                b.getDistanceKm() < 0 ? Double.MAX_VALUE : b.getDistanceKm()));
                        campsAdapter.submit(camps);
                        checkEmpty();
                    }
                    @Override public void onError(String message) {
                        campsAdapter.submit(camps);
                        checkEmpty();
                    }
                });
    }

    private void loadRequests() {
        FirestoreManager.get().fetchActiveRequests(new FirestoreManager.ListCallback<BloodRequest>() {
            @Override public void onLoaded(List<BloodRequest> items) {
                requestsAdapter.submit(items);
                checkEmpty();
            }
            @Override public void onError(String message) {
                requestsAdapter.submit(Collections.emptyList());
                checkEmpty();
            }
        });
    }

    private void checkEmpty() {
        if (empty == null) return;
        boolean none = campsAdapter.getItemCount() == 0
                    && requestsAdapter.getItemCount() == 0;
        empty.setVisibility(none ? View.VISIBLE : View.GONE);
    }

    private void openCamp(BloodCamp c) {
        Intent i = new Intent(getActivity(), BloodCampDetailActivity.class);
        i.putExtra(AppConstants.EXTRA_CAMP, c);
        startActivity(i);
    }

    private void openRequest(BloodRequest r) {
        Intent i = new Intent(getActivity(), BloodRequestDetailActivity.class);
        i.putExtra(AppConstants.EXTRA_REQUEST, r);
        startActivity(i);
    }
}
