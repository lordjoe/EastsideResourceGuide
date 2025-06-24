package com.lordjoe.sandhurst ;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImageAssetDAO {

    private final Connection connection;

    public ImageAssetDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(ImageAsset asset) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("""
            INSERT INTO image_asset (source_id, source_type, image_url)
            VALUES (?, ?, ?)
        """)) {
            stmt.setInt(1, asset.getSourceId());
            stmt.setString(2, asset.getSourceType().toString());
            stmt.setString(3, asset.getImageUrl());
            stmt.executeUpdate();
        }
    }
}
