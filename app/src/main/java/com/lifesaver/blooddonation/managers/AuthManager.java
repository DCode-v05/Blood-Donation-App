package com.lifesaver.blooddonation.managers;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lifesaver.blooddonation.constants.AppConstants;
import com.lifesaver.blooddonation.models.User;
import com.lifesaver.blooddonation.utils.DateUtils;

/**
 * Wraps FirebaseAuth + the /users/{uid} Firestore document.
 * Mirrors context/AuthContext.js — login / register / logout / updateProfile.
 */
public class AuthManager {

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

    public interface ProfileCallback {
        void onLoaded(User profile);
        void onError(String message);
    }

    private static AuthManager INSTANCE;
    public static synchronized AuthManager get() {
        if (INSTANCE == null) INSTANCE = new AuthManager();
        return INSTANCE;
    }

    private final FirebaseAuth      auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db   = FirebaseFirestore.getInstance();

    public FirebaseUser currentUser() { return auth.getCurrentUser(); }
    public boolean      isLoggedIn()  { return currentUser() != null; }

    public void login(@NonNull String email, @NonNull String password,
                      @NonNull AuthCallback cb) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(r -> cb.onSuccess(r.getUser()))
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void register(@NonNull String email, @NonNull String password,
                         @NonNull User profile, @NonNull AuthCallback cb) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(r -> {
                FirebaseUser fu = r.getUser();
                if (fu == null) { cb.onFailure("User creation returned null"); return; }
                profile.setUid(fu.getUid());
                profile.setEmail(email);
                profile.setCreatedAt(DateUtils.today());
                profile.setUpdatedAt(DateUtils.today());
                if (User.ROLE_DONOR.equalsIgnoreCase(profile.getRole())) {
                    profile.setAvailable(true);
                }

                fu.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(profile.getFullName())
                        .build());

                db.collection(AppConstants.COLLECTION_USERS)
                  .document(fu.getUid())
                  .set(profile)
                  .addOnSuccessListener(v -> cb.onSuccess(fu))
                  .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
            })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void loadProfile(@NonNull String uid, @NonNull ProfileCallback cb) {
        db.collection(AppConstants.COLLECTION_USERS)
          .document(uid)
          .get()
          .addOnSuccessListener(doc -> {
              if (!doc.exists()) { cb.onError("Profile not found"); return; }
              User u = doc.toObject(User.class);
              if (u != null) u.setUid(doc.getId());
              cb.onLoaded(u);
          })
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void updateProfile(@NonNull String uid, @NonNull User profile,
                              @NonNull AuthCallback cb) {
        profile.setUpdatedAt(DateUtils.today());
        db.collection(AppConstants.COLLECTION_USERS)
          .document(uid)
          .set(profile)
          .addOnSuccessListener(v -> cb.onSuccess(currentUser()))
          .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void logout() { auth.signOut(); }
}
