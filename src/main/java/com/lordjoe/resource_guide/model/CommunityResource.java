package com.lordjoe.resource_guide.model;

import com.lordjoe.resource_guide.Catagory;

public class CommunityResource {

    private int id;
    private String name;
    private String type; // "Category", "SubCategory", "Resource", "Block"
    private Integer parentId; // Nullable - null for top-level Categories

    // Optional fields
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String website; // Comma-separated websites
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    private String hours;
    private String notes;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public CommunityResource() {
    }

    public CommunityResource(int id, String name, String type, Integer parentId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
    }
    public CommunityResource(  String name, String type, Integer parentId) {
        this(0,name,type,parentId);
    }
    public CommunityResource(  String name, String type, Catagory cat) {
        this(0,name,type,cat.getId());
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPhonePrimary() {
        return phonePrimary;
    }

    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
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

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
