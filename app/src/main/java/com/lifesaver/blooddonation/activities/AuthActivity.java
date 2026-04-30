package com.lifesaver.blooddonation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.managers.AuthManager;
import com.lifesaver.blooddonation.models.User;
import com.lifesaver.blooddonation.utils.ValidationUtils;

public class AuthActivity extends AppCompatActivity {

    private boolean registerMode = false;

    private TextInputLayout layoutFullName, layoutPhone;
    private TextInputEditText editFullName, editPhone, editEmail, editPassword;
    private RadioGroup groupRole;
    private MaterialButton primaryButton;
    private android.widget.TextView toggleText, screenTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_LifeSaver);
        setContentView(R.layout.activity_auth);

        layoutFullName = findViewById(R.id.layout_full_name);
        layoutPhone    = findViewById(R.id.layout_phone);
        editFullName   = findViewById(R.id.edit_full_name);
        editPhone      = findViewById(R.id.edit_phone);
        editEmail      = findViewById(R.id.edit_email);
        editPassword   = findViewById(R.id.edit_password);
        groupRole      = findViewById(R.id.group_role);
        primaryButton  = findViewById(R.id.button_primary);
        toggleText     = findViewById(R.id.text_toggle_mode);
        screenTitle    = findViewById(R.id.text_screen_title);

        primaryButton.setOnClickListener(v -> submit());
        toggleText.setOnClickListener(v -> setMode(!registerMode));
    }

    private void setMode(boolean register) {
        registerMode = register;
        int v = register ? View.VISIBLE : View.GONE;
        layoutFullName.setVisibility(v);
        layoutPhone.setVisibility(v);
        groupRole.setVisibility(v);
        screenTitle.setText(register ? R.string.register : R.string.login);
        primaryButton.setText(register ? R.string.register : R.string.login);
        toggleText.setText(register
                ? R.string.already_have_account : R.string.dont_have_account);
    }

    private void submit() {
        String email    = textOf(editEmail);
        String password = textOf(editPassword);

        if (!ValidationUtils.isValidEmail(email)) {
            toast("Please enter a valid email"); return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            toast("Password must be at least 6 characters"); return;
        }

        primaryButton.setEnabled(false);

        if (!registerMode) {
            AuthManager.get().login(email, password, new AuthManager.AuthCallback() {
                @Override public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                    goToMain();
                }
                @Override public void onFailure(String message) {
                    primaryButton.setEnabled(true);
                    toast("Login failed: " + message);
                }
            });
            return;
        }

        // ---- Register branch ----
        String fullName = textOf(editFullName);
        String phone    = textOf(editPhone);
        if (TextUtils.isEmpty(fullName)) { primaryButton.setEnabled(true); toast("Full name required"); return; }
        if (!ValidationUtils.isValidPhone(phone)) {
            primaryButton.setEnabled(true); toast("Invalid phone number"); return;
        }

        String role;
        int rid = groupRole.getCheckedRadioButtonId();
        if      (rid == R.id.radio_patient) role = User.ROLE_PATIENT;
        else if (rid == R.id.radio_ngo)     role = User.ROLE_NGO;
        else                                role = User.ROLE_DONOR;

        User profile = new User(null, email, fullName, phone, role);
        AuthManager.get().register(email, password, profile, new AuthManager.AuthCallback() {
            @Override public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                goToMain();
            }
            @Override public void onFailure(String message) {
                primaryButton.setEnabled(true);
                toast("Registration failed: " + message);
            }
        });
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private String textOf(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
