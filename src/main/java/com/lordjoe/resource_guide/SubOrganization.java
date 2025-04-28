package com.lordjoe.resource_guide;

/**
 * com.lordjoe.resource_guide.SubOrganization
 * User: Steve
 * Date: 4/27/25
 */

/**
 * com.lordjoe.resource_guide.SubOrganization
 * User: Steve
 * Date: 4/27/25
 */
public class SubOrganization {
    private int id;
    private int parentResourceId;
    private String name;
    private String description;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String website;
    private String notes;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getParentResourceId() { return parentResourceId; }
    public void setParentResourceId(int parentResourceId) { this.parentResourceId = parentResourceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getPhonePrimary() { return phonePrimary; }
    public void setPhonePrimary(String phonePrimary) { this.phonePrimary = phonePrimary; }

    public String getPhoneSecondary() { return phoneSecondary; }
    public void setPhoneSecondary(String phoneSecondary) { this.phoneSecondary = phoneSecondary; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
