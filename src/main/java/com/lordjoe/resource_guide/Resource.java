package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * com.lordjoe.resource_guide.Resource
 * User: Steve
 * Date: 4/28/25
 */
public class Resource extends GuideItem {
    public static final Map<Integer, Resource> cache = new ConcurrentHashMap<>();

    private final int id;
    private final GuideItem parent;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String hours;
    private String notes;

    public static Resource getInstance(int id ) {
        return cache.get(id);
    }
    public static Resource getInstance(int id, String name, GuideItem parent) {
        return cache.computeIfAbsent(id, k -> new Resource(id, name, parent));
    }

    public static Resource getInstance(int id, GuideItem parent) {
        return cache.computeIfAbsent(id, k -> new Resource(id, parent));
    }

    public static Resource getInstance(CommunityResource r, GuideItem parent) {
        return cache.computeIfAbsent(r.getId(), k -> new Resource(r, parent));
    }

    private Resource(int id, String name, GuideItem parent) {
        super(id, name, ResourceType.Resource, parent.getId());
        this.id = id;
        this.parent = parent;
        parent.addChild(this);
    }


    private Resource(int id,   GuideItem parent) {
        super(id, "BLOCK", ResourceType.Block, parent.getId());
        this.id = id;
        this.parent = parent;
        parent.addChild(this);
    }


    private Resource(CommunityResource r,   GuideItem parent) {
        super(r.getId(), r.getName(), ResourceType.Resource, r.getParentId());
        this.id = r.getId();
        this.parent = parent;
        populateFrom(r);
    }

    public void populateFrom(CommunityResource r) {
        description = r.getDescription();
        address = r.getAddress();
        phone = r.getPhone();
        email = r.getEmail();
        website = r.getWebsite();
        hours = r.getHours(); // Added this line
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
        if(description != null && description.contains("A program which provides day"))
            System.out.println(description);
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

    public void reconcile(GuideItem r1) {
        if(! (r1 instanceof  Resource))
            return;
        Resource resource = (Resource)r1;
        if (getName().equals(resource.getName())) {

                setHours(resource.getHours());

                setNotes(resource.getNotes());

                setDescription(resource.getDescription());

                setAddress(resource.getAddress());

                setHours(resource.getHours());
                setWebsite(resource.getWebsite());
                setEmail(resource.getEmail());
            return;
        }

        throw new UnsupportedOperationException("Bad Reconcile"); // ToDo
    }
}
