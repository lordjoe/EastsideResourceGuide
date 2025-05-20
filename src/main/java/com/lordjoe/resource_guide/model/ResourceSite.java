package com.lordjoe.resource_guide.model;

/**
 * com.lordjoe.resource_guide.model.ResourceSite
 * Represents a site (address, contact, website) associated with a resource.
 */
public class ResourceSite {
    private int id;
    private int resourceId;
    private String phone;     // Multi-line or comma-separated
    private String email;
    private String website;   // Comma-separated URLs
    private String address;   // Multi-line address
    private String hours;
    private String notes;


    public ResourceSite(CommunityResource r) {
        this.resourceId = r.getId();
        this.address = r.getAddress();
        this.hours = r.getHours();
        this.phone = r.getPhone();
        this.email = r.getEmail();
        this.website = r.getWebsite();
        this.hours = r.getHours();
        this.notes = r.getNotes();

    }

    public ResourceSite() {
    }

    public ResourceSite(int resourceId) {
        this.resourceId = resourceId;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
