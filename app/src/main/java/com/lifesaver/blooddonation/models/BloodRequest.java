package com.lifesaver.blooddonation.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class BloodRequest implements Serializable {

    public static final String PRIORITY_NORMAL    = "normal";
    public static final String PRIORITY_URGENT    = "urgent";
    public static final String PRIORITY_EMERGENCY = "emergency";

    public static final String STATUS_ACTIVE     = "active";
    public static final String STATUS_FULFILLED  = "fulfilled";
    public static final String STATUS_CANCELLED  = "cancelled";

    @Exclude
    private String id;

    private String patientName;
    private String bloodGroup;
    private String unitsNeeded;
    private String priority;
    private String hospital;
    private String contactNumber;
    private String requiredDate;
    private String description;
    private String createdBy;
    private String creatorName;
    private String status;
    private int responses;
    private String createdAt;
    private String updatedAt;

    public BloodRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getUnitsNeeded() { return unitsNeeded; }
    public void setUnitsNeeded(String unitsNeeded) { this.unitsNeeded = unitsNeeded; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getRequiredDate() { return requiredDate; }
    public void setRequiredDate(String requiredDate) { this.requiredDate = requiredDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getResponses() { return responses; }
    public void setResponses(int responses) { this.responses = responses; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
