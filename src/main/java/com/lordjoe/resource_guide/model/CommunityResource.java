package com.lordjoe.resource_guide.model;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Resource;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityResource {

    private static final Map<Integer,CommunityResource> instances = new HashMap<Integer,CommunityResource>();

    public static List<CommunityResource> getCommunityResources() {
          return new ArrayList<>(instances.values());
    }
    public static CommunityResource getInstance(int id) {
        return instances.get(id);
    }

    // Method to allow DAO to put fully loaded instances into the cache
    public static void putInstance(int id, CommunityResource resource) {
        instances.put(id, resource);
    }
    private final int id;
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

    private CommunityResource(int id) {
        this.id = id;
        instances.put(id,this);
    }

    public CommunityResource reconcile(Resource r) {
        CommunityResource ret = getInstance(r.getId());
        if(ret == null) {
            ret = new CommunityResource(r);
            instances.put(r.getId(), ret);
            return ret;
        }
        else {
            ret.update(r);
            return ret;
        }

    }

    public void update(Resource r) {
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

    private CommunityResource(Resource r) {
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

    public static boolean equivalentText(CommunityResource r1, CommunityResource r2, boolean verbose ) {
        if (r1.id != r2.id) {
            if(verbose) System.out.println("Missd ID");
            return false;
        }
        if (!r1.name.equals(r2.name)) {
            if(verbose) System.out.println("Missd name");
            return false;
        }
        if (r1.type != r2.type) {
            if(verbose) System.out.println("Missd type");
            return false;
        }
        if (r1.parentId != r1.parentId) {
            if(verbose) System.out.println("Missd parentId");
            return false;
        }
        if (!StringUtils.equivalentText(r1.description, r2.description)) {
            if(verbose) System.out.println("Missd description");
            return false;
        }
        if (!StringUtils.equivalentText(r1.address, r2.address)) {
            StringUtils.equivalentText(r1.address, r2.address);
            if(verbose) System.out.println("Missd address");
            return false;
        }
        if (!StringUtils.equivalentText(r1.hours, r2.hours)) {
            if(verbose) System.out.println("Missd hours");
            return false;
        }
        if (!StringUtils.equivalentText(r1.notes, r2.notes)) {
            if(verbose) System.out.println("Missd notes");
            return false;
        }
        if (!StringUtils.equivalentText(r1.email, r2.email)) {
            if(verbose) System.out.println("Missd email");
            return false;
        }
        if (!StringUtils.equivalentText(r1.website, r2.website)) {
            if(verbose) System.out.println("Missd website");
            return false;
        }

        return true;   // they are equivcalewnt;
    }


    public static CommunityResource getResource(int id, String name, ResourceType type, Integer parentId) {
       CommunityResource ret = getInstance(id);
       if (ret == null) {
           ret = new CommunityResource(id);
       }
       ret.setName(name);
       ret.setType(type);
       ret.setParentId(parentId);
       return ret;
    }

    private  static  CommunityResource getResource(String name, ResourceType type, Integer parentId) {
   
            return CommunityResourceDAO.create(0, name, type, parentId);

    }

    private static CommunityResource getResource(String name, ResourceType type, Catagory cat) {
        return getResource(0, name, type, cat.getId());
    }

    // Getters and Setters

    public int getId() {
        return id;
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
