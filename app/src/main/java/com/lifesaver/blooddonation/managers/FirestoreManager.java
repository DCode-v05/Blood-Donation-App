package com.lifesaver.blooddonation.managers;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lifesaver.blooddonation.constants.AppConstants;
import com.lifesaver.blooddonation.models.BloodCamp;
import com.lifesaver.blooddonation.models.BloodRequest;
import com.lifesaver.blooddonation.models.Donation;
import com.lifesaver.blooddonation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Single point of access to the three main Firestore collections.
 */
public class FirestoreManager {

    public interface ListCallback<T> {
        void onLoaded(List<T> items);
        void onError(String message);
    }

    public interface DocCallback<T> {
        void onLoaded(T item);
        void onError(String message);
    }

    public interface CompletionCallback {
        void onSuccess(String id);
        void onError(String message);
    }

    private static FirestoreManager INSTANCE;
    public static synchronized FirestoreManager get() {
        if (INSTANCE == null) INSTANCE = new FirestoreManager();
        return INSTANCE;
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ---- Blood Camps -------------------------------------------------------
    public void fetchActiveCamps(@NonNull ListCallback<BloodCamp> cb) {
        db.collection(AppConstants.COLLECTION_BLOOD_CAMPS)
          .whereEqualTo("status", BloodCamp.STATUS_ACTIVE)
          .orderBy("date", Query.Direction.ASCENDING)
          .get()
          .addOnSuccessListener(snap -> {
              List<BloodCamp> out = new ArrayList<>();
              for (QueryDocumentSnapshot d : snap) {
                  BloodCamp c = d.toObject(BloodCamp.class);
                  c.setId(d.getId());
                  out.add(c);
              }
              cb.onLoaded(out);
          })
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void fetchCamp(@NonNull String id, @NonNull DocCallback<BloodCamp> cb) {
        db.collection(AppConstants.COLLECTION_BLOOD_CAMPS).document(id).get()
          .addOnSuccessListener(d -> {
              if (!d.exists()) { cb.onError("Camp not found"); return; }
              BloodCamp c = d.toObject(BloodCamp.class);
              if (c != null) c.setId(d.getId());
              cb.onLoaded(c);
          })
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void createCamp(@NonNull BloodCamp camp, @NonNull CompletionCallback cb) {
        camp.setStatus(BloodCamp.STATUS_ACTIVE);
        camp.setCreatedAt(DateUtils.today());
        db.collection(AppConstants.COLLECTION_BLOOD_CAMPS).add(camp)
          .addOnSuccessListener(ref -> cb.onSuccess(ref.getId()))
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    // ---- Blood Requests ----------------------------------------------------
    public void fetchActiveRequests(@NonNull ListCallback<BloodRequest> cb) {
        db.collection(AppConstants.COLLECTION_BLOOD_REQUESTS)
          .whereEqualTo("status", BloodRequest.STATUS_ACTIVE)
          .orderBy("createdAt", Query.Direction.DESCENDING)
          .get()
          .addOnSuccessListener(snap -> {
              List<BloodRequest> out = new ArrayList<>();
              for (QueryDocumentSnapshot d : snap) {
                  BloodRequest r = d.toObject(BloodRequest.class);
                  r.setId(d.getId());
                  out.add(r);
              }
              cb.onLoaded(out);
          })
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void fetchRequest(@NonNull String id, @NonNull DocCallback<BloodRequest> cb) {
        db.collection(AppConstants.COLLECTION_BLOOD_REQUESTS).document(id).get()
          .addOnSuccessListener(d -> {
              if (!d.exists()) { cb.onError("Request not found"); return; }
              BloodRequest r = d.toObject(BloodRequest.class);
              if (r != null) r.setId(d.getId());
              cb.onLoaded(r);
          })
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void createRequest(@NonNull BloodRequest req, @NonNull CompletionCallback cb) {
        req.setStatus(BloodRequest.STATUS_ACTIVE);
        req.setCreatedAt(DateUtils.today());
        req.setUpdatedAt(DateUtils.today());
        db.collection(AppConstants.COLLECTION_BLOOD_REQUESTS).add(req)
          .addOnSuccessListener(ref -> cb.onSuccess(ref.getId()))
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void incrementRequestResponses(@NonNull String requestId,
                                          @NonNull CompletionCallback cb) {
        db.collection(AppConstants.COLLECTION_BLOOD_REQUESTS).document(requestId)
          .update("responses",
                  com.google.firebase.firestore.FieldValue.increment(1))
          .addOnSuccessListener(v -> cb.onSuccess(requestId))
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    // ---- Donations ---------------------------------------------------------
    public void fetchDonationsForDonor(@NonNull String donorId,
                                       @NonNull ListCallback<Donation> cb) {
        db.collection(AppConstants.COLLECTION_DONATIONS)
          .whereEqualTo("donorId", donorId)
          .orderBy("date", Query.Direction.DESCENDING)
          .get()
          .addOnSuccessListener(snap -> {
              List<Donation> out = new ArrayList<>();
              for (QueryDocumentSnapshot d : snap) {
                  Donation x = d.toObject(Donation.class);
                  x.setId(d.getId());
                  out.add(x);
              }
              cb.onLoaded(out);
          })
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void recordDonation(@NonNull Donation donation,
                               @NonNull CompletionCallback cb) {
        donation.setCreatedAt(DateUtils.today());
        db.collection(AppConstants.COLLECTION_DONATIONS).add(donation)
          .addOnSuccessListener(ref -> cb.onSuccess(ref.getId()))
          .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }
}
