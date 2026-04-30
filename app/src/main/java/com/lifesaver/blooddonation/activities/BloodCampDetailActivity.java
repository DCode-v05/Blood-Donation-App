package com.lifesaver.blooddonation.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.constants.AppConstants;
import com.lifesaver.blooddonation.managers.AuthManager;
import com.lifesaver.blooddonation.managers.FirestoreManager;
import com.lifesaver.blooddonation.models.BloodCamp;
import com.lifesaver.blooddonation.utils.DateUtils;

public class BloodCampDetailActivity extends AppCompatActivity {

    private TextView name, organizer, dateTime, address, contact, description, registrations;
    private MaterialButton registerBtn, callBtn;
    private BloodCamp camp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_camp_detail);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        tb.setNavigationOnClickListener(v -> finish());

        name          = findViewById(R.id.text_name);
        organizer     = findViewById(R.id.text_organizer);
        dateTime      = findViewById(R.id.text_date_time);
        address       = findViewById(R.id.text_address);
        contact       = findViewById(R.id.text_contact);
        description   = findViewById(R.id.text_description);
        registrations = findViewById(R.id.text_registrations);
        registerBtn   = findViewById(R.id.button_register);
        callBtn       = findViewById(R.id.button_call);

        camp = (BloodCamp) getIntent().getSerializableExtra(AppConstants.EXTRA_CAMP);
        if (camp != null) {
            bind(camp);
            return;
        }
        String id = getIntent().getStringExtra(AppConstants.EXTRA_CAMP_ID);
        if (TextUtils.isEmpty(id)) { finish(); return; }
        FirestoreManager.get().fetchCamp(id, new FirestoreManager.DocCallback<BloodCamp>() {
            @Override public void onLoaded(BloodCamp item) { camp = item; bind(item); }
            @Override public void onError(String message) {
                Toast.makeText(BloodCampDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bind(BloodCamp c) {
        name.setText(c.getName());
        organizer.setText("Organised by " + (c.getOrganizer() == null ? "—" : c.getOrganizer()));
        dateTime.setText("📅 " + DateUtils.formatIso(c.getDate())
                + "   ⏰ " + (c.getStartTime() == null ? "" : c.getStartTime())
                + " — " + (c.getEndTime() == null ? "" : c.getEndTime()));
        address.setText("📍 " + c.getAddress());
        contact.setText("📞 " + c.getContactNumber());
        description.setText(c.getDescription() == null ? "" : c.getDescription());
        registrations.setText("👥 " + c.getRegistrationCount() + " donors registered");

        registerBtn.setOnClickListener(v -> registerForCamp());
        callBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(c.getContactNumber())) return;
            Intent dial = new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + c.getContactNumber()));
            startActivity(dial);
        });
    }

    private void registerForCamp() {
        if (camp == null || camp.getId() == null) return;
        if (AuthManager.get().currentUser() == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = AuthManager.get().currentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection(AppConstants.COLLECTION_BLOOD_CAMPS)
                .document(camp.getId())
                .update("registeredDonors", FieldValue.arrayUnion(uid),
                        "registrationCount", FieldValue.increment(1))
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Registered — see you there!", Toast.LENGTH_SHORT).show();
                    registerBtn.setEnabled(false);
                    registerBtn.setText("Registered ✓");
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Registration failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }
}
