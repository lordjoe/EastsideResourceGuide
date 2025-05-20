package com.lordjoe.resource_guide.model;

public class ResourceDescription {
    private int resourceId;
    private String description;
    private boolean isBlock;

    public ResourceDescription(int resourceId, String description, boolean isBlock) {
        this.resourceId = resourceId;
        this.description = description;
        this.isBlock = isBlock;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getDescription() {
        return description;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
