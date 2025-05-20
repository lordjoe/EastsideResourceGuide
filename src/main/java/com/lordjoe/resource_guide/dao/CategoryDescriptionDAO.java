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
            ps.executeUpdate();
            DatabaseConnection.clearConnection();
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

