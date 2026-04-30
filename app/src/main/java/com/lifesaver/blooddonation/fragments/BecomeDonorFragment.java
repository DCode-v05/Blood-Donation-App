package com.lifesaver.blooddonation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.constants.BloodGroups;
import com.lifesaver.blooddonation.managers.AuthManager;
import com.lifesaver.blooddonation.models.User;

public class BecomeDonorFragment extends Fragment {

    private Spinner spinnerBloodGroup;
    private SwitchMaterial switchAvailable;
    private MaterialButton saveButton;
    private LinearLayout requirementsLayout;
    private User profile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_become_donor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        spinnerBloodGroup  = v.findViewById(R.id.spinner_blood_group);
        switchAvailable    = v.findViewById(R.id.switch_available);
        saveButton         = v.findViewById(R.id.button_save);
        requirementsLayout = v.findViewById(R.id.layout_requirements);

        spinnerBloodGroup.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, BloodGroups.ALL));

        renderRequirements();
        loadProfile();

        saveButton.setOnClickListener(b -> save());
    }

    private void loadProfile() {
        if (AuthManager.get().currentUser() == null) return;
        AuthManager.get().loadProfile(
                AuthManager.get().currentUser().getUid(),
                new AuthManager.ProfileCallback() {
                    @Override public void onLoaded(User u) {
                        profile = u;
                        if (u != null && u.getBloodGroup() != null) {
                            int idx = BloodGroups.ALL.indexOf(u.getBloodGroup());
                            if (idx >= 0) spinnerBloodGroup.setSelection(idx);
                        }
                        switchAvailable.setChecked(u != null && u.isAvailable());
                    }
                    @Override public void onError(String message) { /* silent */ }
                });
    }

    private void save() {
        if (profile == null) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        profile.setRole(User.ROLE_DONOR);
        profile.setBloodGroup((String) spinnerBloodGroup.getSelectedItem());
        profile.setAvailable(switchAvailable.isChecked());

        AuthManager.get().updateProfile(profile.getUid(), profile,
                new AuthManager.AuthCallback() {
                    @Override public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                        Toast.makeText(getContext(), "Profile saved",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(String message) {
                        Toast.makeText(getContext(), message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderRequirements() {
        String[][] reqs = {
                {"Age",     "18–65 years old"},
                {"Weight",  "Minimum 50 kg"},
                {"Health",  "Good general health, no recent illness"},
                {"Iron",    "Adequate hemoglobin (tested before donation)"},
                {"Cooling", "Wait period since last donation respected"},
        };
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (String[] r : reqs) {
            View row = inflater.inflate(R.layout.item_fact, requirementsLayout, false);
            TextView tv = row.findViewById(R.id.text_fact);
            tv.setText(r[0] + " — " + r[1]);
            requirementsLayout.addView(row);
        }
    }
}
