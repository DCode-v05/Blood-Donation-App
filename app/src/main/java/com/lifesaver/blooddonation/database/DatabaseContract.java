package com.lifesaver.blooddonation.database;

import android.provider.BaseColumns;

/**
 * Schema for the local SQLite cache. The same data lives in Firestore;
 * SQLite is used for offline display.
 */
public final class DatabaseContract {
    private DatabaseContract() {}

    public static final String DATABASE_NAME    = "lifesaver.db";
    public static final int    DATABASE_VERSION = 1;

    public static class CampEntry implements BaseColumns {
        public static final String TABLE = "blood_camps";
        public static final String COL_ID         = "id";
        public static final String COL_NAME       = "name";
        public static final String COL_ORGANIZER  = "organizer";
        public static final String COL_DATE       = "date";
        public static final String COL_START_TIME = "start_time";
        public static final String COL_END_TIME   = "end_time";
        public static final String COL_ADDRESS    = "address";
        public static final String COL_LATITUDE   = "latitude";
        public static final String COL_LONGITUDE  = "longitude";
        public static final String COL_CONTACT    = "contact_number";
        public static final String COL_STATUS     = "status";
        public static final String COL_REG_COUNT  = "registration_count";
    }

    public static class RequestEntry implements BaseColumns {
        public static final String TABLE = "blood_requests";
        public static final String COL_ID            = "id";
        public static final String COL_PATIENT_NAME  = "patient_name";
        public static final String COL_BLOOD_GROUP   = "blood_group";
        public static final String COL_UNITS         = "units_needed";
        public static final String COL_PRIORITY      = "priority";
        public static final String COL_HOSPITAL      = "hospital";
        public static final String COL_CONTACT       = "contact_number";
        public static final String COL_REQUIRED_DATE = "required_date";
        public static final String COL_CREATED_BY    = "created_by";
        public static final String COL_STATUS        = "status";
        public static final String COL_RESPONSES     = "responses";
    }

    public static class DonationEntry implements BaseColumns {
        public static final String TABLE = "donations";
        public static final String COL_ID            = "id";
        public static final String COL_DONOR_ID      = "donor_id";
        public static final String COL_DONOR_NAME    = "donor_name";
        public static final String COL_BLOOD_GROUP   = "donor_blood_group";
        public static final String COL_DATE          = "date";
        public static final String COL_LOCATION      = "location";
        public static final String COL_UNITS         = "units";
        public static final String COL_TYPE          = "type";
        public static final String COL_ORGANIZATION  = "organization";
        public static final String COL_NOTES         = "notes";
    }
}
