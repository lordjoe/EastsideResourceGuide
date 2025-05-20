package com.lordjoe.resource_guide.model;

import java.time.LocalDateTime;

public class ResourceUrl {
    private int id;
    private int resourceId;
    private String urlType;
    private String url;
    private Boolean isValidated;
    private Boolean isValid;
    private LocalDateTime lastChecked;

    public ResourceUrl() {}

    public ResourceUrl(int resourceId, String urlType, String url) {
        this.resourceId = resourceId;
        this.urlType = urlType;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean validated) {
        isValidated = validated;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean valid) {
        isValid = valid;
    }

    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }
// Getters and Setters omitted for brevity
}

