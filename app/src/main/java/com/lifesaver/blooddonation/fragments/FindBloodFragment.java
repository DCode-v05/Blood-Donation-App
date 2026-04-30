package com.lifesaver.blooddonation.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.activities.BloodRequestDetailActivity;
import com.lifesaver.blooddonation.adapters.BloodRequestAdapter;
import com.lifesaver.blooddonation.constants.AppConstants;
import com.lifesaver.blooddonation.constants.BloodGroups;
import com.lifesaver.blooddonation.managers.AuthManager;
import com.lifesaver.blooddonation.managers.FirestoreManager;
import com.lifesaver.blooddonation.models.BloodRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindBloodFragment extends Fragment {

    private static final String FILTER_ALL = "All blood groups";

    private BloodRequestAdapter adapter;
    private TextView empty;
    private List<BloodRequest> all = new ArrayList<>();
    private String selectedFilter = FILTER_ALL;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_blood, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        empty = v.findViewById(R.id.text_empty);
        adapter = new BloodRequestAdapter(this::openRequest);

        RecyclerView rv = v.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        Spinner filter = v.findViewById(R.id.spinner_blood_group);
        List<String> options = new ArrayList<>();
        options.add(FILTER_ALL);
        options.addAll(BloodGroups.ALL);
        filter.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, options));
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View view, int pos, long id) {
                selectedFilter = options.get(pos);
                applyFilter();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        ((MaterialButton) v.findViewById(R.id.button_create))
                .setOnClickListener(b -> showCreateDialog());

        load();
    }

    private void load() {
        FirestoreManager.get().fetchActiveRequests(new FirestoreManager.ListCallback<BloodRequest>() {
            @Override public void onLoaded(List<BloodRequest> items) {
                all = items;
                applyFilter();
            }
            @Override public void onError(String message) {
                all = new ArrayList<>();
                applyFilter();
            }
        });
    }

    private void applyFilter() {
        List<BloodRequest> shown;
        if (FILTER_ALL.equals(selectedFilter)) {
            shown = all;
        } else {
            shown = new ArrayList<>();
            for (BloodRequest r : all) {
                if (selectedFilter.equalsIgnoreCase(r.getBloodGroup())) shown.add(r);
            }
        }
        adapter.submit(shown);
        empty.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openRequest(BloodRequest r) {
        Intent i = new Intent(getActivity(), BloodRequestDetailActivity.class);
        i.putExtra(AppConstants.EXTRA_REQUEST, r);
        startActivity(i);
    }

    private void showCreateDialog() {
        if (AuthManager.get().currentUser() == null) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_create_request, null, false);

        Spinner sBlood    = dialogView.findViewById(R.id.spinner_blood_group);
        Spinner sPriority = dialogView.findViewById(R.id.spinner_priority);
        sBlood.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, BloodGroups.ALL));
        sPriority.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("normal", "urgent", "emergency")));

        new AlertDialog.Builder(requireContext())
                .setTitle("Create Blood Request")
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (d, w) -> submit(dialogView, sBlood, sPriority))
                .show();
    }

    private void submit(View dv, Spinner sBlood, Spinner sPriority) {
        EditText name        = dv.findViewById(R.id.edit_patient_name);
        EditText units       = dv.findViewById(R.id.edit_units);
        EditText hospital    = dv.findViewById(R.id.edit_hospital);
        EditText contact     = dv.findViewById(R.id.edit_contact);
        EditText reqDate     = dv.findViewById(R.id.edit_required_date);
        EditText description = dv.findViewById(R.id.edit_description);

        BloodRequest r = new BloodRequest();
        r.setPatientName(name.getText().toString().trim());
        r.setBloodGroup((String) sBlood.getSelectedItem());
        r.setUnitsNeeded(units.getText().toString().trim());
        r.setPriority((String) sPriority.getSelectedItem());
        r.setHospital(hospital.getText().toString().trim());
        r.setContactNumber(contact.getText().toString().trim());
        r.setRequiredDate(reqDate.getText().toString().trim());
        r.setDescription(description.getText().toString().trim());
        r.setCreatedBy(AuthManager.get().currentUser().getUid());
        r.setCreatorName(AuthManager.get().currentUser().getDisplayName());

        FirestoreManager.get().createRequest(r, new FirestoreManager.CompletionCallback() {
            @Override public void onSuccess(String id) {
                Toast.makeText(getContext(), "Request posted!", Toast.LENGTH_SHORT).show();
                load();
            }
            @Override public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
