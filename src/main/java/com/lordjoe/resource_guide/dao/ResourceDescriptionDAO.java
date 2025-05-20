package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceDescriptionDAO {

    public static void insert(ResourceDescription description) throws SQLException {
        String sql = "INSERT INTO resource_descriptions (resource_id, content, is_block) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, description.getResourceId());
            stmt.setString(2, description.getDescription());
            stmt.setBoolean(3, description.isBlock());

            stmt.executeUpdate();
            DatabaseConnection.clearConnection();
            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
   

public static Map<Integer, List<CommunityResource>> loadAllAsMap() throws SQLException {
    Map<Integer, List<CommunityResource>> result = new HashMap<>();

    String sql = "SELECT * FROM community_resources";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            CommunityResource resource = new CommunityResource();
            resource.setId(rs.getInt("id"));
            resource.setName(rs.getString("name"));
            resource.setType(ResourceType.valueOf(rs.getString("type")));
            resource.setParentId(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);

            List<CommunityResource> l = result.get(resource.getId());
            if (l == null) {
                l = new ArrayList<>();
                result.put(resource.getId(), l);
            }
            l.add(resource);
        }
        DatabaseConnection.clearConnection();
    }
    return result;
}

public static Map<Integer, List<ResourceDescription>> loadGroupedByResourceX() throws SQLException {
    Map<Integer, List<ResourceDescription>> result = new HashMap<>();

    String sql = "SELECT * FROM resource_descriptions";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            ResourceDescription resource = new ResourceDescription(rs.getInt("resource_id"), rs.getString("content"), rs.getBoolean("is_block"));

            List<ResourceDescription> l = result.get(resource.getResourceId());
            if (l == null) {
                l = new ArrayList<>();
                result.put(resource.getResourceId(), l);
            }
            l.add(resource);
        }
        DatabaseConnection.clearConnection();
    }
    return result;
}

}
