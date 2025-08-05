package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.ResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all items (Category, SubCategory, Resource, Block).
 */
public class GuideItem implements Comparable<GuideItem> {
    private final int id;
    private final String name;
    private String description;  // Optional
    final private ResourceType type;
    private   Integer parentId;
    private boolean block;       // true if this description is a formatted block
    private final List<Resource> blocks = new ArrayList<>();
    private final Map<String, GuideItem> NameToResource = new HashMap<>();
    private final List<GuideItem> resources = new ArrayList<>();

    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ResourceType getType() {
        return type;
    }



    public int getParentId() {
        return parentId;
    }

    public void setParentId(Integer id) {
        parentId  = id;
    }

    public GuideItem(int id, String name, ResourceType type, Integer parentId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
    }

    public void dropChild(GuideItem resource) {
        String name = resource.getName();
        NameToResource.remove(name);
    }
    public void addChild(GuideItem resource) {
        String name = resource.getName();
        resource.setParentId(id);
        if(name.equals(getName())) {
            System.out.println("Duplicate name: " + name);
        }
        if (NameToResource.containsKey(name)) {
            if(resource instanceof Resource) {
                ((Resource) resource).reconcile(NameToResource.get(name));
            }
        } else {
            NameToResource.put(name, resource);
            resources.add(resource);
        }
    }

    public GuideItem getChild(String name) {
        return NameToResource.get(name);
    }

    public void addBlock(Resource blockText) {
        if(!blocks.isEmpty())
            System.out.println(blockText.getDescription());
        blocks.add(blockText);
    }

    public List<GuideItem> getChildren() {
        return resources;
    }

    public boolean hasChildren() {
        return !resources.isEmpty();
    }


    public List<Resource> getResources() {
        List<Resource> ret = new ArrayList<>();
        for (GuideItem resource : resources) {
            if(resource instanceof Resource) {
                ret.add((Resource) resource);
            }
        }
        return ret;
    }

    public List<Resource> getBlocks() {
        return blocks;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isBlock() {
        return block;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void markAsBlock() {
        this.block = true;
    }

    public Catagory getCatagory() {
        if (this instanceof Catagory) {
            return (Catagory) this;
        }
        throw new IllegalStateException("Not a Catagory: " + name);
    }

    @Override
    public int compareTo(GuideItem other) {
        return this.name.compareToIgnoreCase(other.name);
    }
}
