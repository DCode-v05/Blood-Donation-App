package com.lifesaver.blooddonation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.managers.AuthManager;
import com.lifesaver.blooddonation.models.User;
import com.lifesaver.blooddonation.utils.ValidationUtils;

public class ProfileActivity extends AppCompatActivity {

    private TextView initials, name, roleEmail, phone, bloodGroup, availability;
    private MaterialButton logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        tb.setNavigationOnClickListener(v -> finish());

        initials     = findViewById(R.id.text_initials);
        name         = findViewById(R.id.text_name);
        roleEmail    = findViewById(R.id.text_role_email);
        phone        = findViewById(R.id.text_phone);
        bloodGroup   = findViewById(R.id.text_blood_group);
        availability = findViewById(R.id.text_availability);
        logoutBtn    = findViewById(R.id.button_logout);

        logoutBtn.setOnClickListener(v -> {
            AuthManager.get().logout();
            Intent i = new Intent(this, AuthActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        loadProfile();
    }

    private void loadProfile() {
        if (AuthManager.get().currentUser() == null) { finish(); return; }
        String uid = AuthManager.get().currentUser().getUid();
        AuthManager.get().loadProfile(uid, new AuthManager.ProfileCallback() {
            @Override public void onLoaded(User u) { bind(u); }
            @Override public void onError(String message) { finish(); }
        });
    }

    private void bind(User u) {
        if (u == null) return;
        initials.setText(ValidationUtils.initials(u.getFullName()));
        name.setText(u.getFullName());
        roleEmail.setText(u.getRole().toUpperCase() + "  •  " + u.getEmail());
        phone.setText("📞 " + (u.getPhone() == null ? "—" : u.getPhone()));
        bloodGroup.setText("🩸 " + (u.getBloodGroup() == null ? "Not set" : u.getBloodGroup()));
        if (u.isDonor()) {
            availability.setText(u.isAvailable() ? "✅ Available to donate" : "⏸ Currently unavailable");
            availability.setVisibility(android.view.View.VISIBLE);
        } else {
            availability.setVisibility(android.view.View.GONE);
        }
    }
}
