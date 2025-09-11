package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.Resource;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;

@Controller
public class ResourceUpdateController {

    @PostMapping("/update-resource")
    public RedirectView updateResource(
            @RequestParam("id") Integer id,
            @RequestParam(value = "parentId", required = false) Integer parentId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "hours", required = false) String hours,
            @RequestParam(value = "notes", required = false) String notes
    ) {

        // ---------- A) ensure we have an id ----------
        CommunityResource cr = (id != null && id > 0) ? CommunityResource.getInstance(id) : null;
        boolean isNew = (cr == null);

        if (isNew) {
            // Create DB row (write only) and get the generated id back
            cr = CommunityResourceDAO.create(
                    0, nullToEmpty(name), ResourceType.Resource, parentId, null
            );
            id = cr.getId();
        }

        // ---------- B) update in-memory CommunityResource (NO DB READS) ----------
        cr.setName(nullToEmpty(name));
        cr.setParentId(parentId);
        cr.setPhone(nullToEmpty(phone));
        cr.setEmail(nullToEmpty(email));
        cr.setWebsite(nullToEmpty(website));
        cr.setAddress(nullToEmpty(address));
        cr.setHours(nullToEmpty(hours));
        cr.setNotes(nullToEmpty(notes));

        // Persist the CR (write only)
        CommunityResourceDAO.update(cr);

        // ---------- C) update description (deletes previous non-blocks, then insert) ----------
        String desc = description == null ? "" : description;
        if(desc.length() > 0) {
            ResourceDescription d = new ResourceDescription(id, desc, false);
            try {
                ResourceDescriptionDAO.insert(d);
            } catch (SQLException e) {
                throw new RuntimeException(e);

            }
        }

        // ---------- D) update cached Resource (NO DB READS) ----------
        Resource cached = Resource.getInstance(id);
        if (cached == null) {
            cached = Resource.getInstance(id); // your singleton accessor; if it creates, fine
        }
        // keep these two lines even if populateFrom copies most fields,
        // since you've said BOTH must be updated explicitly
        cached.populateFrom(cr);
        cached.setDescription(desc);

        // If your Guide caches lists/maps, make them consistent here if you have helpers:
        // Guide.Instance.refreshResource(cr);  // (call only if it exists)

        // ---------- E) redirect to category page (absolute path) ----------
        Integer redirectCategoryId = resolveCategoryId(parentId, id);
        String target = (redirectCategoryId != null) ? "/main/category?id=" + redirectCategoryId : "/main";
        RedirectView rv = new RedirectView(target, true);
        rv.setExposeModelAttributes(false);
        return rv;
    }

    @PostMapping("/delete-resource")
    public RedirectView deleteResource(@RequestParam("id") int id,
                                       @RequestParam(value = "parentId", required = false) Integer parentId) {

        Integer redirectCategoryId = resolveCategoryId(parentId, id);

   
        // drop from caches (NO DB READS)
        CommunityResource cachedCR = CommunityResource.getInstance(id);
         if (cachedCR != null) {
             CommunityResource.dropInstance(cachedCR);
          }

        String target = (redirectCategoryId != null) ? "/main/category?id=" + redirectCategoryId : "/main";
        RedirectView rv = new RedirectView(target, true);
        rv.setExposeModelAttributes(false);
        return rv;
    }

    // -------- helpers --------

    private static String nullToEmpty(String s) { return s == null ? "" : s.trim(); }

    /** Prefer provided parentId; otherwise walk up using YOUR cached objects—no DB reads. */
    private static Integer resolveCategoryId(Integer parentId, Integer resourceId) {
        if (parentId != null) {
            Catagory cat = Guide.Instance.getCatagoryById(parentId);
            if (cat != null) return cat.getId();
        }
        if (resourceId == null) return null;

        CommunityResource res = Guide.Instance.getResourceById(resourceId);
        if (res == null) return null;

        Integer p = res.getParentId();
        if (p == null) return null;

        CommunityResource parent = Guide.Instance.getResourceById(p);
        if (parent == null) return null;

        switch (parent.getType()) {
            case Category -> { return parent.getId(); }
            case Subcategory -> {
                Integer catId = parent.getParentId();
                return (catId != null) ? catId : parent.getId();
            }
            default -> {
                Integer up = parent.getParentId();
                return (up != null) ? up : parent.getId();
            }
        }
    }
}
