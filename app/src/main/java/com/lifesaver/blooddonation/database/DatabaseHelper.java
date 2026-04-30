package com.lifesaver.blooddonation.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.lifesaver.blooddonation.database.DatabaseContract.CampEntry;
import com.lifesaver.blooddonation.database.DatabaseContract.DonationEntry;
import com.lifesaver.blooddonation.database.DatabaseContract.RequestEntry;
import com.lifesaver.blooddonation.models.BloodCamp;
import com.lifesaver.blooddonation.models.Donation;
import com.lifesaver.blooddonation.models.GeoLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite open-helper for offline cache + the donations log.
 * Donations live in both Firestore (synced) and SQLite (local fallback).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper INSTANCE;

    public static synchronized DatabaseHelper getInstance(@NonNull Context ctx) {
        if (INSTANCE == null) INSTANCE = new DatabaseHelper(ctx.getApplicationContext());
        return INSTANCE;
    }

    private DatabaseHelper(Context ctx) {
        super(ctx, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CampEntry.TABLE + " (" +
                CampEntry.COL_ID         + " TEXT PRIMARY KEY, " +
                CampEntry.COL_NAME       + " TEXT, " +
                CampEntry.COL_ORGANIZER  + " TEXT, " +
                CampEntry.COL_DATE       + " TEXT, " +
                CampEntry.COL_START_TIME + " TEXT, " +
                CampEntry.COL_END_TIME   + " TEXT, " +
                CampEntry.COL_ADDRESS    + " TEXT, " +
                CampEntry.COL_LATITUDE   + " REAL, " +
                CampEntry.COL_LONGITUDE  + " REAL, " +
                CampEntry.COL_CONTACT    + " TEXT, " +
                CampEntry.COL_STATUS     + " TEXT, " +
                CampEntry.COL_REG_COUNT  + " INTEGER)");

        db.execSQL("CREATE TABLE " + RequestEntry.TABLE + " (" +
                RequestEntry.COL_ID            + " TEXT PRIMARY KEY, " +
                RequestEntry.COL_PATIENT_NAME  + " TEXT, " +
                RequestEntry.COL_BLOOD_GROUP   + " TEXT, " +
                RequestEntry.COL_UNITS         + " TEXT, " +
                RequestEntry.COL_PRIORITY      + " TEXT, " +
                RequestEntry.COL_HOSPITAL      + " TEXT, " +
                RequestEntry.COL_CONTACT       + " TEXT, " +
                RequestEntry.COL_REQUIRED_DATE + " TEXT, " +
                RequestEntry.COL_CREATED_BY    + " TEXT, " +
                RequestEntry.COL_STATUS        + " TEXT, " +
                RequestEntry.COL_RESPONSES     + " INTEGER)");

        db.execSQL("CREATE TABLE " + DonationEntry.TABLE + " (" +
                DonationEntry.COL_ID            + " TEXT PRIMARY KEY, " +
                DonationEntry.COL_DONOR_ID      + " TEXT, " +
                DonationEntry.COL_DONOR_NAME    + " TEXT, " +
                DonationEntry.COL_BLOOD_GROUP   + " TEXT, " +
                DonationEntry.COL_DATE          + " TEXT, " +
                DonationEntry.COL_LOCATION      + " TEXT, " +
                DonationEntry.COL_UNITS         + " TEXT, " +
                DonationEntry.COL_TYPE          + " TEXT, " +
                DonationEntry.COL_ORGANIZATION  + " TEXT, " +
                DonationEntry.COL_NOTES         + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CampEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RequestEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DonationEntry.TABLE);
        onCreate(db);
    }

    // ---- Donations ---------------------------------------------------------
    public long insertDonation(Donation d) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(DonationEntry.COL_ID,           d.getId());
        v.put(DonationEntry.COL_DONOR_ID,     d.getDonorId());
        v.put(DonationEntry.COL_DONOR_NAME,   d.getDonorName());
        v.put(DonationEntry.COL_BLOOD_GROUP,  d.getDonorBloodGroup());
        v.put(DonationEntry.COL_DATE,         d.getDate());
        v.put(DonationEntry.COL_LOCATION,     d.getLocation());
        v.put(DonationEntry.COL_UNITS,        d.getUnits());
        v.put(DonationEntry.COL_TYPE,         d.getType());
        v.put(DonationEntry.COL_ORGANIZATION, d.getOrganization());
        v.put(DonationEntry.COL_NOTES,        d.getNotes());
        return db.insertWithOnConflict(DonationEntry.TABLE, null, v,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Donation> getDonationsByDonor(String donorId) {
        List<Donation> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(DonationEntry.TABLE, null,
                DonationEntry.COL_DONOR_ID + "=?", new String[]{donorId},
                null, null, DonationEntry.COL_DATE + " DESC")) {
            while (c.moveToNext()) {
                Donation d = new Donation();
                d.setId(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_ID)));
                d.setDonorId(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_DONOR_ID)));
                d.setDonorName(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_DONOR_NAME)));
                d.setDonorBloodGroup(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_BLOOD_GROUP)));
                d.setDate(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_DATE)));
                d.setLocation(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_LOCATION)));
                d.setUnits(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_UNITS)));
                d.setType(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_TYPE)));
                d.setOrganization(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_ORGANIZATION)));
                d.setNotes(c.getString(c.getColumnIndexOrThrow(DonationEntry.COL_NOTES)));
                out.add(d);
            }
        }
        return out;
    }

    // ---- Camps offline cache ----------------------------------------------
    public void cacheCamp(BloodCamp c) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(CampEntry.COL_ID,         c.getId());
        v.put(CampEntry.COL_NAME,       c.getName());
        v.put(CampEntry.COL_ORGANIZER,  c.getOrganizer());
        v.put(CampEntry.COL_DATE,       c.getDate());
        v.put(CampEntry.COL_START_TIME, c.getStartTime());
        v.put(CampEntry.COL_END_TIME,   c.getEndTime());
        v.put(CampEntry.COL_ADDRESS,    c.getAddress());
        if (c.getLocation() != null) {
            v.put(CampEntry.COL_LATITUDE,  c.getLocation().getLatitude());
            v.put(CampEntry.COL_LONGITUDE, c.getLocation().getLongitude());
        }
        v.put(CampEntry.COL_CONTACT,   c.getContactNumber());
        v.put(CampEntry.COL_STATUS,    c.getStatus());
        v.put(CampEntry.COL_REG_COUNT, c.getRegistrationCount());
        db.insertWithOnConflict(CampEntry.TABLE, null, v,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<BloodCamp> getCachedCamps() {
        List<BloodCamp> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(CampEntry.TABLE, null, null, null,
                null, null, CampEntry.COL_DATE + " ASC")) {
            while (c.moveToNext()) {
                BloodCamp camp = new BloodCamp();
                camp.setId(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_ID)));
                camp.setName(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_NAME)));
                camp.setOrganizer(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_ORGANIZER)));
                camp.setDate(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_DATE)));
                camp.setStartTime(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_START_TIME)));
                camp.setEndTime(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_END_TIME)));
                camp.setAddress(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_ADDRESS)));
                camp.setLocation(new GeoLocation(
                        c.getDouble(c.getColumnIndexOrThrow(CampEntry.COL_LATITUDE)),
                        c.getDouble(c.getColumnIndexOrThrow(CampEntry.COL_LONGITUDE)),
                        camp.getAddress()));
                camp.setContactNumber(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_CONTACT)));
                camp.setStatus(c.getString(c.getColumnIndexOrThrow(CampEntry.COL_STATUS)));
                camp.setRegistrationCount(c.getInt(c.getColumnIndexOrThrow(CampEntry.COL_REG_COUNT)));
                out.add(camp);
            }
        }
        return out;
    }
}
