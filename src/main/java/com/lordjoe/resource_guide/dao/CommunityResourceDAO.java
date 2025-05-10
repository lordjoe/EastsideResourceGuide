package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityResourceDAO {

    public static int insert(CommunityResource resource) throws SQLException {
        if(resource.getId() != 0) {
            return resource.getId();
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO community_resources (name, type, parent_id) VALUES (?, ?, ?) RETURNING id");
            stmt.setString(1, resource.getName());
            String string = resource.getType().toString();
            stmt.setString(2, string);
            if (resource.getParentId() != null) {
                stmt.setInt(3, resource.getParentId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DatabaseConnection.clearConnection();
                return rs.getInt("id");
            }
            throw new SQLException("Insert failed, no ID obtained.");
            // keep connection  open
          }
     }



    public static Map<Integer, CommunityResource> loadAllAsMap() throws SQLException {
        Map<Integer, CommunityResource> result = new HashMap<>();

          String sql = "SELECT * FROM community_resources";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CommunityResource resource = new CommunityResource();
                resource.setId(rs.getInt("id"));
                resource.setName(rs.getString("name"));
                resource.setType(ResourceType.Resource.valueOf(rs.getString("type")));
                resource.setParentId(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);

                result.put(resource.getId(), resource);
            }
        }
        return result;
    }


    public static  CommunityResource  getCommunityResource(String name) throws SQLException {

        String sql = "SELECT * FROM community_resources where name = " + "name ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CommunityResource resource = new CommunityResource();
                resource.setId(rs.getInt("id"));
                resource.setName(rs.getString("name"));
                resource.setType(ResourceType.valueOf(rs.getString("type")));
                resource.setParentId(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);

                return resource;
            }
            return null;
        }
      }



    public static List<CommunityResource> loadAll() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM community_resources");
        ResultSet rs = stmt.executeQuery();
        List<CommunityResource> results = new ArrayList<>();
        while (rs.next()) {
            CommunityResource cr = new CommunityResource();
            cr.setId(rs.getInt("id"));
            cr.setName(rs.getString("name"));
            cr.setType(ResourceType.valueOf(rs.getString("type")));
            int pid = rs.getInt("parent_id");
            if (!rs.wasNull()) cr.setParentId(pid);
            results.add(cr);
        }
        return results;
    }

    public static void deleteAll() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM community_resources");
        stmt.executeUpdate();
    }
}
