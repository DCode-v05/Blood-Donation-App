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
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.constants.AppConstants;
import com.lifesaver.blooddonation.managers.FirestoreManager;
import com.lifesaver.blooddonation.models.BloodRequest;
import com.lifesaver.blooddonation.utils.DateUtils;

public class BloodRequestDetailActivity extends AppCompatActivity {

    private TextView bloodGroup, patientName, priority,
            units, hospital, requiredDate, description;
    private MaterialButton respondBtn, callBtn;
    private BloodRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request_detail);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        tb.setNavigationOnClickListener(v -> finish());

        bloodGroup   = findViewById(R.id.text_blood_group);
        patientName  = findViewById(R.id.text_patient_name);
        priority     = findViewById(R.id.text_priority);
        units        = findViewById(R.id.text_units);
        hospital     = findViewById(R.id.text_hospital);
        requiredDate = findViewById(R.id.text_required_date);
        description  = findViewById(R.id.text_description);
        respondBtn   = findViewById(R.id.button_respond);
        callBtn      = findViewById(R.id.button_call);

        request = (BloodRequest) getIntent().getSerializableExtra(AppConstants.EXTRA_REQUEST);
        if (request != null) { bind(request); return; }

        String id = getIntent().getStringExtra(AppConstants.EXTRA_REQUEST_ID);
        if (TextUtils.isEmpty(id)) { finish(); return; }
        FirestoreManager.get().fetchRequest(id, new FirestoreManager.DocCallback<BloodRequest>() {
            @Override public void onLoaded(BloodRequest item) { request = item; bind(item); }
            @Override public void onError(String message) {
                Toast.makeText(BloodRequestDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bind(BloodRequest r) {
        bloodGroup.setText(r.getBloodGroup());
        patientName.setText(r.getPatientName());
        priority.setText(r.getPriority() == null ? "" : r.getPriority().toUpperCase());
        switch (r.getPriority() == null ? "" : r.getPriority()) {
            case BloodRequest.PRIORITY_NORMAL:
                priority.setBackgroundResource(R.drawable.bg_priority_normal); break;
            case BloodRequest.PRIORITY_URGENT:
                priority.setBackgroundResource(R.drawable.bg_priority_urgent); break;
            default:
                priority.setBackgroundResource(R.drawable.bg_priority_emergency);
        }

        units.setText("🩸 " + r.getUnitsNeeded() + " unit(s) needed");
        hospital.setText("🏥 " + r.getHospital());
        requiredDate.setText("📅 Needed by " + DateUtils.formatIso(r.getRequiredDate()));
        description.setText(r.getDescription() == null ? "" : r.getDescription());

        respondBtn.setOnClickListener(v -> respond());
        callBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(r.getContactNumber())) return;
            startActivity(new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + r.getContactNumber())));
        });
    }

    private void respond() {
        if (request == null || request.getId() == null) return;
        FirestoreManager.get().incrementRequestResponses(request.getId(),
                new FirestoreManager.CompletionCallback() {
                    @Override public void onSuccess(String id) {
                        Toast.makeText(BloodRequestDetailActivity.this,
                                "Thank you — patient has been notified!",
                                Toast.LENGTH_LONG).show();
                        respondBtn.setEnabled(false);
                        respondBtn.setText("Response Sent ✓");
                    }
                    @Override public void onError(String message) {
                        Toast.makeText(BloodRequestDetailActivity.this,
                                message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
