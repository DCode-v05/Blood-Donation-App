package com.lifesaver.blooddonation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.fragments.AwarenessFragment;
import com.lifesaver.blooddonation.fragments.BecomeDonorFragment;
import com.lifesaver.blooddonation.fragments.FindBloodFragment;
import com.lifesaver.blooddonation.fragments.HomeFragment;
import com.lifesaver.blooddonation.fragments.TrackerFragment;
import com.lifesaver.blooddonation.managers.AuthManager;
import com.lifesaver.blooddonation.models.User;

public class MainActivity extends AppCompatActivity {

    private User profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this::onToolbar);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(this::onNav);

        // Default tab
        if (savedInstanceState == null) replace(new HomeFragment());

        loadProfile();
    }

    private void loadProfile() {
        if (AuthManager.get().currentUser() == null) return;
        String uid = AuthManager.get().currentUser().getUid();
        AuthManager.get().loadProfile(uid, new AuthManager.ProfileCallback() {
            @Override public void onLoaded(User p) {
                profile = p;
                BottomNavigationView nav = findViewById(R.id.bottom_nav);
                // Hide Tracker tab for non-donor accounts
                nav.getMenu().findItem(R.id.nav_tracker)
                        .setVisible(p != null && p.isDonor());
            }
            @Override public void onError(String message) { /* silent */ }
        });
    }

    private boolean onNav(@NonNull MenuItem item) {
        Fragment f;
        int id = item.getItemId();
        if      (id == R.id.nav_home)           f = new HomeFragment();
        else if (id == R.id.nav_find_blood)     f = new FindBloodFragment();
        else if (id == R.id.nav_become_donor)   f = new BecomeDonorFragment();
        else if (id == R.id.nav_tracker)        f = new TrackerFragment();
        else if (id == R.id.nav_awareness)      f = new AwarenessFragment();
        else return false;
        replace(f);
        return true;
    }

    private boolean onToolbar(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_chat) {
            startActivity(new Intent(this, ChatBotActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return false;
    }

    private void replace(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit();
    }

    public User getProfile() { return profile; }
}
