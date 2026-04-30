package com.lifesaver.blooddonation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.lifesaver.blooddonation.managers.AuthManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Splash uses windowBackground from theme; no setContentView needed.
        new Handler(Looper.getMainLooper()).postDelayed(this::routeNext, SPLASH_MS);
    }

    private void routeNext() {
        Intent next = AuthManager.get().isLoggedIn()
                ? new Intent(this, MainActivity.class)
                : new Intent(this, AuthActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(next);
        finish();
    }
}
