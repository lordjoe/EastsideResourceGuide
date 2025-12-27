package com.lordjoe.resource_guide.dao;


import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDescriptionDAO {

    public static void insert(String category, String subcategory, String description) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO category_descriptions (category_name, subcategory_name, description) VALUES (?, ?, ?)")) {
            ps.setString(1, category);
            ps.setString(2, subcategory);
            ps.setString(3, description);
            if(description.contains("A program which provides day"))
                System.out.println(description);
            ps.executeUpdate();
            DatabaseConnection.clearConnection();
        }
    }

    // CommunityResourceDAO.java

    public static void deleteSubtree(int rootId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                deleteSubtreeInternal(conn, rootId);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error deleting subtree under id=" + rootId, e);
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error deleting subtree for id=" + rootId, e);
        }
    }

    private static void deleteSubtreeInternal(Connection conn, int resourceId) throws SQLException {
        // 1. Recursively delete children
        String selectChildren = "SELECT id FROM community_resources WHERE parent_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectChildren)) {
            ps.setInt(1, resourceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int childId = rs.getInt("id");
                    deleteSubtreeInternal(conn, childId);
                }
            }
        }

        // 2. Delete descriptions for this resourceId
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM resource_descriptions WHERE resource_id = ?")) {
            ps.setInt(1, resourceId);
            ps.executeUpdate();
        }

        // 3. Delete sites for this resourceId
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM resource_sites WHERE resource_id = ?")) {
            ps.setInt(1, resourceId);
            ps.executeUpdate();
        }

        // 4. Delete the resource record itself
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM community_resources WHERE id = ?")) {
            ps.setInt(1, resourceId);
            ps.executeUpdate();
        }

        // 5. Clear any cached CommunityResource instance
        com.lordjoe.resource_guide.model.CommunityResource instance =
                com.lordjoe.resource_guide.model.CommunityResource.getInstance(resourceId);
        if (instance != null) {
            com.lordjoe.resource_guide.model.CommunityResource.dropInstance(instance);
        }
    }


    public static List<String> loadDescriptions(String category, String subcategory) throws SQLException {
        List<String> descriptions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT description FROM category_descriptions WHERE category_name = ? AND (subcategory_name IS NULL OR subcategory_name = ?)")) {
            ps.setString(1, category);
            ps.setString(2, subcategory);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    descriptions.add(rs.getString("description"));
                }
            }
        }
        return descriptions;
    }

}

