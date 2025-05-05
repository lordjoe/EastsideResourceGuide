package com.lordjoe.resource_guide.model;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Resource;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.util.StringUtils;

public class CommunityResource {

    private int id;
    private String name;
    private ResourceType type; // "Category", "SubCategory", "Resource", "Block"
    private Integer parentId; // Nullable - null for top-level Categories

    // Optional fields
    private String phone;
    private String email;
    private String website; // Comma-separated websites
    private String address;
    private String hours;
    private String notes;
    private String description;

    public boolean hasSiteInfo() {
        if (hours != null)
            return true;
        if (email != null)
            return true;
        if (phone != null)
            return true;
        if (address != null)
            return true;
        if (website != null)
            return true;
        if (notes != null)
            return true;
        return false;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public CommunityResource() {
    }

    public CommunityResource(Resource r) {
        id = r.getId();
        type = ResourceType.Resource;
        name = r.getName();
        parentId = getParentId();
        phone = r.getPhone();
        email = r.getEmail();
        website = r.getWebsite();
        address = r.getAddress();
        hours = r.getHours();
        notes = r.getNotes();
        description = r.getDescription();

    }

    public static boolean equivalentText(CommunityResource r1, CommunityResource r2) {
        if (r1.id != r2.id)
            return false;
        if (!r1.name.equals(r2.name))
            return false;
        if (r1.type != r2.type)
            return false;
        if (r1.parentId != r1.parentId)
            return false;
        if (!StringUtils.equivalentText(r1.description, r2.description))
            return false;
        if (!StringUtils.equivalentText(r1.address, r2.address))
            return false;
        if (!StringUtils.equivalentText(r1.hours, r2.hours))
            return false;
        if (!StringUtils.equivalentText(r1.notes, r2.notes))
            return false;
        if (!StringUtils.equivalentText(r1.email, r2.email))
            return false;
        if (!StringUtils.equivalentText(r1.website, r2.website))
            return false;

        return true;   // they are equivcalewnt;
    }


    public CommunityResource(int id, String name, ResourceType type, Integer parentId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
    }

    public CommunityResource(String name, ResourceType type, Integer parentId) {
        this(0, name, type, parentId);
    }

    public CommunityResource(String name, ResourceType type, Catagory cat) {
        this(0, name, type, cat.getId());
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

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
