package com.lordjoe.sandhurst ;

import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImageAssetDAO {

    private final Connection connection;

    public ImageAssetDAO(Connection connection) {
        this.connection = connection;
    }



        public static int insert(ImageAsset asset) throws SQLException {
            if(asset.getId() != 0) {
                return asset.getId();
            }
            try (Connection conn = DatabaseConnection.getConnection()){
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO image_asset (source_id, source_type, image_url) VALUES (?, ?, ?) RETURNING id"
                 );

                String name = asset.getSourceType().name();
                stmt.setString(2, name);
                stmt.setInt(1, asset.getSourceId());
                stmt.setString(3, asset.getImageUrl());

                 ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    DatabaseConnection.clearConnection();
                    int id = rs.getInt("id");
                    asset.setId(id);
                    return id;
                }
                throw new SQLException("Insert failed, no ID obtained.");
                // keep connection  open
            }

        }


   
    public List<ImageAsset> getImagesForInhabitant(int inhabitantId) {
        List<ImageAsset> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM image_asset WHERE source_id = ? AND source_type = 'Inhabitant'")) {
            stmt.setInt(1, inhabitantId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching images", e);
        }
        return results;
    }

    private ImageAsset fromResultSet(ResultSet rs) throws SQLException {
        ImageAsset asset = new ImageAsset();
        asset.setId(rs.getInt("id"));
        asset.setSourceId(rs.getInt("source_id"));
        asset.setSourceType(ImageAssetType.valueOf(rs.getString("source_type")));
        asset.setImageUrl(rs.getString("image_url"));
        return asset;
    }

}
