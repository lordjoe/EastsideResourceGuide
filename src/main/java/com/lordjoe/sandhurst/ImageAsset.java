package com.lordjoe.sandhurst;

public class ImageAsset {
    private int id;
    private int sourceId;
    private ImageAssetType sourceType;
    private String imageUrl;

    public ImageAsset() {
        // Needed for DAO and deserialization
    }

    public ImageAsset(int id, ImageAssetType type, int sourceId, String imageUrl) {
        this.id = id;
        this.sourceType = type;
        this.sourceId = sourceId;
        this.imageUrl = imageUrl;
    }
    public ImageAsset(int sourceId, ImageAssetType sourceType, String imageUrl) {
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSourceId() { return sourceId; }
    public void setSourceId(int sourceId) { this.sourceId = sourceId; }

    public ImageAssetType getSourceType() { return sourceType; }
    public void setSourceType(ImageAssetType sourceType) { this.sourceType = sourceType; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
