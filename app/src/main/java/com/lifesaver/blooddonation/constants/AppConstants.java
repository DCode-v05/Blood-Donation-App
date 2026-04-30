package com.lifesaver.blooddonation.constants;

public final class AppConstants {
    private AppConstants() {}

    // Firestore collection names
    public static final String COLLECTION_USERS          = "users";
    public static final String COLLECTION_BLOOD_CAMPS    = "blood_camps";
    public static final String COLLECTION_BLOOD_REQUESTS = "blood_requests";
    public static final String COLLECTION_DONATIONS      = "donations";

    // Default search radius for nearby items, in km
    public static final double DEFAULT_RADIUS_KM = 50.0;

    // Intent extras
    public static final String EXTRA_CAMP_ID    = "extra_camp_id";
    public static final String EXTRA_REQUEST_ID = "extra_request_id";
    public static final String EXTRA_CAMP       = "extra_camp";
    public static final String EXTRA_REQUEST    = "extra_request";

    // FCM notification channel
    public static final String NOTIFICATION_CHANNEL_ID = "lifesaver_default";

    // Permission request codes
    public static final int RC_LOCATION_PERMISSION    = 1001;
    public static final int RC_NOTIFICATION_PERMISSION = 1002;
}
