package com.lifesaver.blooddonation.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

/**
 * User profile stored in Firestore /users/{uid}.
 * Mirrors the original React Native userDoc schema.
 */
public class User implements Serializable {

    public static final String ROLE_DONOR   = "donor";
    public static final String ROLE_PATIENT = "patient";
    public static final String ROLE_NGO     = "ngo";

    private String uid;
    private String email;
    private String fullName;
    private String phone;
    private String role;

    private String bloodGroup;
    private GeoLocation location;
    private boolean isAvailable;

    private String organizationName;
    private String organizationType;

    private String pushToken;
    private String createdAt;
    private String updatedAt;

    public User() {}

    public User(String uid, String email, String fullName, String phone, String role) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public GeoLocation getLocation() { return location; }
    public void setLocation(GeoLocation location) { this.location = location; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getOrganizationType() { return organizationType; }
    public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }

    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Exclude
    public boolean isDonor() {
        return ROLE_DONOR.equalsIgnoreCase(role);
    }

    @Exclude
    public boolean isNgo() {
        return ROLE_NGO.equalsIgnoreCase(role);
    }

    @Exclude
    public boolean isPatient() {
        return ROLE_PATIENT.equalsIgnoreCase(role);
    }
}
