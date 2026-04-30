package com.lifesaver.blooddonation.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BloodCamp implements Serializable {

    public static final String STATUS_ACTIVE    = "active";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    @Exclude
    private String id;

    private String name;
    private String organizer;
    private String date;
    private String startTime;
    private String endTime;
    private String address;
    private GeoLocation location;
    private String contactNumber;
    private String description;
    private String status;
    private List<String> registeredDonors = new ArrayList<>();
    private int registrationCount;
    private String createdAt;

    /** Computed client-side; not persisted. */
    @Exclude private double distanceKm = -1;

    public BloodCamp() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public GeoLocation getLocation() { return location; }
    public void setLocation(GeoLocation location) { this.location = location; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getRegisteredDonors() { return registeredDonors; }
    public void setRegisteredDonors(List<String> registeredDonors) {
        this.registeredDonors = registeredDonors == null ? new ArrayList<>() : registeredDonors;
    }

    public int getRegistrationCount() { return registrationCount; }
    public void setRegistrationCount(int registrationCount) { this.registrationCount = registrationCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Exclude public double getDistanceKm() { return distanceKm; }
    @Exclude public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
}
