package com.lifesaver.blooddonation.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lifesaver.blooddonation.adapters.DonationHistoryAdapter;
import com.lifesaver.blooddonation.constants.BloodGroups;
import com.lifesaver.blooddonation.database.DatabaseHelper;
import com.lifesaver.blooddonation.managers.AuthManager;
import com.lifesaver.blooddonation.managers.FirestoreManager;
import com.lifesaver.blooddonation.models.Donation;
import com.lifesaver.blooddonation.models.User;
import com.lifesaver.blooddonation.utils.DateUtils;
import com.lifesaver.blooddonation.utils.DonationEligibility;

import java.util.ArrayList;
import java.util.List;

public class TrackerFragment extends Fragment {

    private DonationHistoryAdapter adapter;
    private TextView eligibilityText, totalText, emptyText;
    private User profile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        eligibilityText = v.findViewById(R.id.text_eligibility);
        totalText       = v.findViewById(R.id.text_total);
        emptyText       = v.findViewById(R.id.text_empty);

        adapter = new DonationHistoryAdapter();
        RecyclerView rv = v.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        ((MaterialButton) v.findViewById(R.id.button_log))
                .setOnClickListener(b -> showLogDialog());

        loadProfileThenDonations();
    }

    private void loadProfileThenDonations() {
        if (AuthManager.get().currentUser() == null) return;
        AuthManager.get().loadProfile(
                AuthManager.get().currentUser().getUid(),
                new AuthManager.ProfileCallback() {
                    @Override public void onLoaded(User u) {
                        profile = u;
                        loadDonations();
                    }
                    @Override public void onError(String message) { loadDonations(); }
                });
    }

    private void loadDonations() {
        if (AuthManager.get().currentUser() == null) return;
        String uid = AuthManager.get().currentUser().getUid();
        FirestoreManager.get().fetchDonationsForDonor(uid,
                new FirestoreManager.ListCallback<Donation>() {
                    @Override public void onLoaded(List<Donation> items) { bind(items); }
                    @Override public void onError(String message) {
                        // Fall back to local cache
                        if (getContext() != null) {
                            bind(DatabaseHelper.getInstance(getContext()).getDonationsByDonor(uid));
                        } else {
                            bind(new ArrayList<>());
                        }
                    }
                });
    }

    private void bind(List<Donation> donations) {
        adapter.submit(donations);
        emptyText.setVisibility(donations.isEmpty() ? View.VISIBLE : View.GONE);
        totalText.setText("Total donations: " + donations.size());

        Donation last = donations.isEmpty() ? null : donations.get(0);
        if (last == null) {
            eligibilityText.setText("✅ Eligible to donate now");
            return;
        }
        DonationEligibility.Result r = DonationEligibility.compute(last.getDate(), last.getType());
        if (r.eligible) {
            eligibilityText.setText("✅ Eligible to donate now");
        } else {
            eligibilityText.setText("⏳ Eligible in " + r.daysUntilEligible
                    + " days (next: " + DateUtils.format(r.nextEligibleDate) + ")");
        }
    }

    private void showLogDialog() {
        if (profile == null) {
            Toast.makeText(getContext(), "Profile not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_log_donation, null, false);
        Spinner type = v.findViewById(R.id.spinner_type);

        List<String> labels = new ArrayList<>();
        for (BloodGroups.DonationType t : BloodGroups.DONATION_TYPES) labels.add(t.label);
        type.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, labels));

        new AlertDialog.Builder(requireContext())
                .setTitle("Log a Donation")
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, (d, w) -> submitLog(v, type))
                .show();
    }

    private void submitLog(View dv, Spinner typeSpinner) {
        String typeLabel = (String) typeSpinner.getSelectedItem();
        String typeValue = "whole_blood";
        for (BloodGroups.DonationType t : BloodGroups.DONATION_TYPES) {
            if (t.label.equals(typeLabel)) typeValue = t.value;
        }
        EditText date  = dv.findViewById(R.id.edit_date);
        EditText loc   = dv.findViewById(R.id.edit_location);
        EditText units = dv.findViewById(R.id.edit_units);
        EditText org   = dv.findViewById(R.id.edit_organization);

        Donation donation = new Donation();
        donation.setDonorId(profile.getUid());
        donation.setDonorName(profile.getFullName());
        donation.setDonorBloodGroup(profile.getBloodGroup());
        donation.setType(typeValue);
        donation.setDate(date.getText().toString().trim());
        donation.setLocation(loc.getText().toString().trim());
        donation.setUnits(units.getText().toString().trim());
        donation.setOrganization(org.getText().toString().trim());

        FirestoreManager.get().recordDonation(donation,
                new FirestoreManager.CompletionCallback() {
                    @Override public void onSuccess(String id) {
                        donation.setId(id);
                        if (getContext() != null) {
                            DatabaseHelper.getInstance(getContext()).insertDonation(donation);
                        }
                        Toast.makeText(getContext(), "Donation logged",
                                Toast.LENGTH_SHORT).show();
                        loadDonations();
                    }
                    @Override public void onError(String message) {
                        Toast.makeText(getContext(), message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
