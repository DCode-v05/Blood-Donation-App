package com.lifesaver.blooddonation.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Donation implements Serializable {

    public static final String TYPE_WHOLE_BLOOD = "whole_blood";
    public static final String TYPE_PLATELETS   = "platelets";
    public static final String TYPE_PLASMA      = "plasma";
    public static final String TYPE_RED_CELLS   = "red_cells";

    @Exclude
    private String id;

    private String donorId;
    private String donorName;
    private String donorBloodGroup;
    private String date;
    private String location;
    private String units;
    private String type;
    private String organization;
    private String notes;
    private String createdAt;

    public Donation() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getDonorBloodGroup() { return donorBloodGroup; }
    public void setDonorBloodGroup(String donorBloodGroup) { this.donorBloodGroup = donorBloodGroup; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
