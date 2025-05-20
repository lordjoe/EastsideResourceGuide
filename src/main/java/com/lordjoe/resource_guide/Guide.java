package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceDescriptionDAO;
import com.lordjoe.resource_guide.dao.ResourceSiteDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import com.lordjoe.resource_guide.model.ResourceSite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the complete guide structure from the database into memory.
 */
public class Guide {
    public static final Guide Instance = new Guide();

    private final Map<Integer, GuideItem> idToCatagory = new HashMap<>();
    private final Map<String, GuideItem> nameToCatagory = new HashMap<>();
    private final Map<Integer, GuideItem> idToSubCatagory = new HashMap<>();
    private final Map<Integer, Resource> idToResource = new HashMap<>();
    private final List<Catagory> catagories = new ArrayList<>();

    private boolean loaded = false;

    private Guide() {
    }

    public void guaranteeLoaded() {
        if (!loaded) {
            try {
                loadFromDatabase();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load guide data", e);
            }
        }
    }

    public List<Catagory> getCatagories() {
        guaranteeLoaded();
        return catagories;
    }

    public Catagory getCatagoryByName(String name) {
        GuideItem guideItem = nameToCatagory.get(name);
        if(guideItem instanceof Catagory)
             return (Catagory) guideItem;
        return null;
    }

    private void loadFromDatabase() throws SQLException {
        Map<Integer, CommunityResource> allResources = CommunityResourceDAO.loadAllAsMap();
        Map<Integer, List<ResourceDescription>> descriptions = ResourceDescriptionDAO.loadGroupedByResourceX();
        Map<Integer, ResourceSite> sites = ResourceSiteDAO.loadAllAsMap();

        // First pass: categories and subcategories
        for (CommunityResource cr : allResources.values()) {
            ResourceType type = cr.getType();
            switch (type) {
                case Category -> {
                    Catagory cat = new Catagory(cr.getId(), cr.getName());
                    cat.setDescription(mergeDescriptions(descriptions.get(cr.getId())));
                    catagories.add(cat);
                    idToCatagory.put(cr.getId(), cat);
                    nameToCatagory.put(cr.getName(), cat);
                }
                case Subcategory -> {
                    GuideItem parentX = idToCatagory.get(cr.getParentId());
                    if(!(parentX instanceof Catagory))
                        return;
                    Catagory parent = (Catagory) parentX;
                    if (parent != null) {
                        SubCatagory sub = new SubCatagory(cr.getId(), cr.getName(), parent);
                        sub.setDescription(mergeDescriptions(descriptions.get(cr.getId())));
                        idToSubCatagory.put(cr.getId(), sub);
                        parent.addSubCatagory(sub);
                    }
                }
                default -> {
                }
            }
        }

        // Second pass: resources and blocks
        for (CommunityResource cr : allResources.values()) {
            ResourceType type = cr.getType();
            if (type == ResourceType.Resource) {
                GuideItem parent = resolveParent(cr.getParentId());
                if (parent == null) continue;

                Resource res = new Resource(cr.getId(), cr.getName(), parent);

                res.setDescription(mergeDescriptions(descriptions.get(cr.getId())));

                ResourceSite site = sites.get(cr.getId());
                if (site != null) {
                    res.setAddress(site.getAddress());
                    res.setPhone(site.getPhone());
                    res.setEmail(site.getEmail());
                    res.setWebsite(site.getWebsite());
                    res.setHours(site.getHours());
                    res.setNotes(site.getNotes());
                }

                parent.addChild(res);
                int id = cr.getId();
                if (idToResource.containsKey(id)) {
                    throw new UnsupportedOperationException("Fix This"); // ToDo
                }
                idToResource.put(id, res);
            }
            if (type == ResourceType.Block) {
                GuideItem parent = resolveParent(cr.getParentId());
                if (parent == null) continue;

                Resource res = new Resource(cr.getId(), cr.getName(), parent);

                int id = cr.getId();
                List<ResourceDescription> descriptions1 = descriptions.get(id);
                String description = mergeDescriptions(descriptions1);
                res.setDescription(description);
                parent.addBlock(res);
                idToResource.put(cr.getId(), res);

            }
        }

        loaded = true;
    }

    private GuideItem resolveParent(Integer parentId) {
        if (parentId == null) return null;
        if (idToCatagory.containsKey(parentId))
            return idToCatagory.get(parentId);
        if (idToSubCatagory.containsKey(parentId))
            return idToSubCatagory.get(parentId);
        return idToResource.get(parentId);
    }

    private String mergeDescriptions(List<ResourceDescription> descriptions) {
        if (descriptions == null || descriptions.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (ResourceDescription d : descriptions) {
            sb.append(d.getDescription()).append("\n");
        }
        return sb.toString().trim();
    }
}
