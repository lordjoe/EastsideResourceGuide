package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.util.StringUtils;

/**
 * com.lordjoe.resource_guide.Resource
 * User: Steve
 * Date: 4/28/25
 */
public class Resource extends GuideItem {
    private final int id;
    private final GuideItem parent;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String hours;
    private String notes;

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }


    public boolean equivalentText(Resource r1, Resource r2) {
        if (r1.parent != null) {
            if (r2.parent != null) {
                if (r1.parent.getId() != r2.getParent().getId())
                    return false;
            }
            {
                return false;
            }
        }
        if (!StringUtils.equivalentText(r1.description, r2.description))
            return false;
        if (!StringUtils.equivalentText(r1.phone, r2.phone))
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

    public Resource(int id, String name, GuideItem parent) {
        super(id, name, ResourceType.Resource, parent.getId());
        this.id = id;
        this.parent = parent;
        parent.addChild(this);
    }


    public void populateFrom(CommunityResource r) {
        description = r.getDescription();
        address = r.getAddress();
        phone = r.getPhone();
        email = r.getEmail();
        website = r.getWebsite();
        notes = r.getNotes();
    }

    public int getId() {
        return id;
    }

    public GuideItem getParent() {
        return parent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phonePrimary) {
        this.phone = phonePrimary;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void reconcile(GuideItem r1) {
        if(! (r1 instanceof  Resource))
            return;
        Resource resource = (Resource)r1;
        if (getName().equals(resource.getName())) {
            if (getHours() == null)
                setHours(resource.getHours());
            if (getNotes() == null)
                setNotes(resource.getNotes());
            if (getDescription() == null)
                setDescription(resource.getDescription());
            if (getAddress() == null)
                setAddress(resource.getAddress());
            if (getHours() == null)
                setHours(resource.getHours());
            if (getEmail() == null)
                setEmail(resource.getEmail());
            return;
        }

        throw new UnsupportedOperationException("Bad Reconcile"); // ToDo
    }
}
