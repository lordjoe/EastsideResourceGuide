package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.*;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import com.lordjoe.resource_guide.model.ResourceSite;
import com.lordjoe.resource_guide.security.GuideUser;
import com.lordjoe.resource_guide.util.DatabaseConnection;
import com.lordjoe.resource_guide.util.URLValidator;
import com.lordjoe.utilities.Encrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lordjoe.resource_guide.Database.createDatabase;

/**
 * Loads the complete guide structure from the database into memory.
 */
public class Guide {
    public static final Guide Instance = new Guide();

    private final Map<Integer, GuideItem> idToCatagory = new HashMap<>();
    private final Map<String, GuideItem> nameToCatagory = new HashMap<>();
    private final Map<Integer, Resource> idToResource = new HashMap<>();
    private final Map<Integer, Resource> idToBlock = new HashMap<>();
    private final List<Catagory> catagories = new ArrayList<>();
    private final Map<String, GuideUser> userMap = new HashMap<>();

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

    public void reload() {
        if ( loaded) {
           loaded = false;
            idToCatagory.clear();
            userMap.clear();
            idToBlock.clear();
            idToResource.clear();
            nameToCatagory.clear();
         }
        guaranteeLoaded();
    }


    public List<Catagory> getCatagories() {
        guaranteeLoaded();
        return catagories;
    }

    public List<Catagory> getSubCatagories() {
       List<Catagory> ret = new ArrayList<>();
        for (Catagory catagory : getCatagories()) {
            List<SubCatagory> subCatagories = catagory.getSubCatagories();
            ret.addAll(subCatagories);
        }
        return ret;
    }

    public Catagory getUnClassified() {
        for (Catagory catagory : getCatagories()) {
            if(catagory.getName().equalsIgnoreCase("unclassified"))
                return catagory;
        }
        return null;
    }

    public List<Catagory> getClassified() {
        List<Catagory> ret = new ArrayList<>(getCatagories());
        ret.remove(getUnClassified());
        return ret;
    }

    public List<Resource> getCommunityResources() {
        guaranteeLoaded();
        return new ArrayList<>(idToResource.values());
    }

    public CommunityResource getResourceById(int id) {
        guaranteeLoaded();
        Resource r = idToResource.get(id);
        return new CommunityResource(r);
    }

    public Catagory getCatagoryByName(String name) {
        GuideItem guideItem = nameToCatagory.get(name);
        if (guideItem instanceof Catagory)
            return (Catagory) guideItem;
        return null;
    }

    public void addResource(CommunityResource r) {
        idToResource.remove(r.getId());

        Integer parentId = r.getParentId();
        Catagory catagoryById = getCatagoryById(parentId);
        Resource rx = new Resource(r, catagoryById);
         idToResource.put(r.getId(), rx);
         GuideItem guideItem = idToCatagory.get(r.getParentId());
         if (guideItem == null) {
             guideItem.addChild(rx);
         }
    }


    public void removeResource(CommunityResource r) {
        idToResource.remove(r.getId());
         GuideItem guideItem = idToCatagory.get(r.getParentId());
        Resource rx = new Resource(r,guideItem);
        if (guideItem == null) {
            guideItem.dropChild(rx);
        }
    }


    public Catagory getCatagoryById(Integer id) {
        GuideItem guideItem = idToCatagory.get(id);
        if (guideItem instanceof Catagory)
            return (Catagory) guideItem;
        return null;
    }

    public SubCatagory getSubCatagoryById(Integer id) {
        GuideItem guideItem = idToCatagory.get(id);
        if (guideItem instanceof SubCatagory)
            return (SubCatagory) guideItem;
        return null;
    }

    public Catagory getTopLevelCategory(int id) {
        CommunityResource current = getResourceById(id);
        while (current != null && current.getParentId() != null) {
            current = getResourceById(current.getParentId());
        }
        return getCatagoryById(current.getId()); // current should now be the root category
    }

    private void loadFromDatabase() throws SQLException {
        Map<Integer, CommunityResource> allResources = CommunityResourceDAO.loadAllAsMap();
        Map<Integer, List<ResourceDescription>> descriptions = ResourceDescriptionDAO.loadGroupedByResourceX();
        Map<Integer, ResourceSite> sites = ResourceSiteDAO.loadAllAsMap();
        Map<Integer, List<ResourceDescription>> blockd = mapBlockResources(descriptions);

        createDatabase(); // make sure tablews exist
        // First pass: categories and subcategories
        for (CommunityResource cr : allResources.values()) {
            ResourceType type = cr.getType();
            switch (type) {
                case Category -> {
                    String name = cr.getName();
                    Catagory cat = new Catagory(cr.getId(), name);
                    int id = cr.getId();
                    List<ResourceDescription> descriptions1 = descriptions.get(id);
                    String description = mergeDescriptions(descriptions1);
                    cat.setDescription(description);
                    List<ResourceDescription> blocks = blockd.get(id);
                    addBlocks(blocks, cat);
                    catagories.add(cat);
                    idToCatagory.put(id, cat);
                    nameToCatagory.put(cr.getName(), cat);
                }
                case Subcategory -> {
                    GuideItem parentX = idToCatagory.get(cr.getParentId());
                    if (!(parentX instanceof Catagory))
                        return;
                    Catagory parent = (Catagory) parentX;
                    String name = cr.getName();
                    int id1 = cr.getId();
                    SubCatagory sub = new SubCatagory(id1, name, parent);
                    idToCatagory.put(id1, sub);
                    parent.addSubCatagory(sub);
                    List<ResourceDescription> descriptions1 = descriptions.get(id1);
                    String description = mergeDescriptions(descriptions1);
                    sub.setDescription(description);
                    List<ResourceDescription> blocks = blockd.get(id1);
                    addBlocks(blocks, sub);

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

                int id1 = cr.getId();
                List<ResourceDescription> descriptions1 = descriptions.get(id1);
                String description = mergeDescriptions(descriptions1);
                res.setDescription(description);
                List<ResourceDescription> blocks = blockd.get(id1);
                addBlocks(blocks, res);

                ResourceSite site = sites.get(cr.getId());
                if (site != null) {
                    res.setAddress(site.getAddress());
                    res.setPhone(site.getPhone());
                    res.setEmail(site.getEmail());
                    res.setWebsite(site.getWebsite());
                    res.setHours(site.getHours());
                    res.setNotes(site.getNotes());
                }
                String url = res.getWebsite();

                if(url != null) {
                   boolean valid = URLValidator.isValidURL(url);
                   if(!valid && !CheckURLValidation.KnownBadURLS.contains(url) ) {
                       valid = URLValidator.isValidURL(url);
                       System.out.println("\"" + url + "\",");
                   }
                }

                parent.addChild(res);
                int id = cr.getId();
                if (idToResource.containsKey(id)) {
                    Resource resource = idToResource.get(id);
                    continue;
                   // throw new UnsupportedOperationException("Fix This"); // ToDo
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
                idToBlock.put(cr.getId(), res);

            }
        }
        loadUsers();
        loaded = true;
    }

    public void loadUsers() {
        userMap.clear();
        UserDAO.guaranteeUser("admin", Encrypt.encryptString("Sulphur32"));
        UserDAO.guaranteeUser("lordjoe2000@gmail.com", Encrypt.encryptString("Sulphur32"));

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, password, role FROM users")) {

            while (rs.next()) {
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                String role = rs.getString("role");
                GuideUser user = new GuideUser(username, encryptedPassword, role);
                userMap.put(username, user);
            }
         } catch (SQLException e) {
            throw new RuntimeException("Failed to load users", e);
        }
    }

    public GuideUser getUserByUsername(String username) {
        return userMap.get(username);
    }
    
    private Map<Integer, List<ResourceDescription>> mapBlockResources(Map<Integer, List<ResourceDescription>> descriptions) {
        if (descriptions == null)
            return null;

        Map<Integer, List<ResourceDescription>> idToBlocks = new HashMap<>();
        for (Integer i : descriptions.keySet()) {
            List<ResourceDescription> list = descriptions.get(i);
            if (list == null)
                continue;
            List<ResourceDescription> remove = new ArrayList<>();
            for (ResourceDescription resourceDescription : list) {
                if (resourceDescription.isBlock()) {
                    remove.add(resourceDescription);
                }
            }
            if (!remove.isEmpty()) {
                list.removeAll(remove);
                idToBlocks.put(i, remove);
            }
        }
        return idToBlocks;
    }

    private void addBlocks(List<ResourceDescription> descriptions1, GuideItem parent) {
        if (descriptions1 != null && !descriptions1.isEmpty()) {
            for (ResourceDescription resourceDescription : descriptions1) {
                if (resourceDescription.isBlock()) {
                    Resource resx = new Resource(resourceDescription.getResourceId(), parent);
                    resx.setDescription(resourceDescription.getDescription());

                    parent.addBlock(resx);
                }
            }
        }
    }

    private GuideItem resolveParent(Integer parentId) {
        if (parentId == null) return null;
        if (idToCatagory.containsKey(parentId))
            return idToCatagory.get(parentId);
        return idToResource.get(parentId);
    }

    private String mergeDescriptions(List<ResourceDescription> descriptions) {
        if (descriptions == null || descriptions.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (ResourceDescription d : descriptions) {
            if (!d.isBlock())
                sb.append(d.getDescription()).append("\n");
        }
        return sb.toString().trim();
    }

    private String fixBropenURL(Resource res) {
        String[] items = res.getWebsite().split(" ");
        res.setWebsite(items[0]);
        if(items.length > 1)  {
            String test = items[1];
            if(test.contains("Hours")) {
                res.setHours(items[1]);
            }
            else if(test.contains("Address")) {
               test = test.replace("Address","");
                   res.setAddress(test.trim());
            }
        }
        CommunityResource data = new CommunityResource(res);
        CommunityResourceDAO.update(data);
        return res.getWebsite();
    }

    private void validateURLs() {
        List<String >  badURLS = new ArrayList<>();
        List<String >  goodURLS = new ArrayList<>();

        for (Resource value : idToResource.values()) {
            String website = value.getWebsite();
            if (website != null) {
                boolean needsCheck = com.lordjoe.resource_guide.util.URLValidator.needsCheck(website);
                if(website.contains(" ")) {
                   fixBropenURL(value);
                }
                needsCheck = !CheckURLValidation.KnownGoodURLS.contains(website);
                if (needsCheck) {
                    boolean valid = com.lordjoe.resource_guide.util.URLValidator.isValidURL(website);
                    if (!valid)
                        badURLS.add(website);
                    else
                        goodURLS.add(website);

                }

            }
        }
        System.out.println("Good URLs  " );
        for (String goodURL : goodURLS) {
            System.out.println("\"" + goodURL + "\",");
        }
        System.out.println("Bad URLs  " );
        for (String goodURL : badURLS) {
            System.out.println("\"" + goodURL + "\",");        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            if (args[0].equals("remote")) {
                DatabaseConnection.setREMOTE();
            }
        }
        Guide.Instance.guaranteeLoaded();
        Guide.Instance.validateURLs();
        for (Catagory catagory : Guide.Instance.catagories) {
            System.out.println(catagory.getName());
        }
    }

}
