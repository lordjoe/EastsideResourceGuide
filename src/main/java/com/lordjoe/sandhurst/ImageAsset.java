package com.lordjoe.sandhurst;

public class ImageAsset {
    private int id;
    private int sourceId;
    private ImageAssetType sourceType; // "house" or "inhabitant"
    private String imageUrl;

    public ImageAsset(int sourceId,ImageAssetType sourceType, String imageUrl) {
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.imageUrl = imageUrl;
    }

    public int getSourceId() {
        return sourceId;
    }

    public ImageAssetType getSourceType() {
        return sourceType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
