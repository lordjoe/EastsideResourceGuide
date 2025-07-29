package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.SubCatagory;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.model.CommunityResource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * com.lordjoe.resource_guide.util.CategoryUtils
 * User: Steve
 * Date: 4/30/25
 */
public class CategoryUtils {
    public static Catagory CreateCatagory(String name) throws SQLException {
        try(Connection conn = DatabaseConnection.getConnection()) {
            String type = "Category";
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO community_resources (name, type, parent_id) VALUES (?, ?, ?) RETURNING id"
            );
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setNull(3, java.sql.Types.INTEGER);  // explicitly set parent_id to NULL
            
            ResultSet rs = stmt.executeQuery();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("id");
            }
            return new Catagory(id,name );
        }
        catch (SQLException e)    {
            throw new RuntimeException(e);
        }
    }

    public static Integer getCategoryByName(String name) throws SQLException {
        String query = """
            SELECT id FROM community_resources
            WHERE name = ?  
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
      
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null; // Category not found
    }

    public static SubCatagory CreateSubCatagory(String name,Catagory parent) throws SQLException {
        try(Connection conn = DatabaseConnection.getConnection()) {
            String type = "Category";
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO community_resources (name, type, parent.getId()) VALUES (?, ?, ?) RETURNING id");

            ResultSet rs = stmt.executeQuery();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("id");
            }
            return new SubCatagory(id,name ,parent);
        }
        catch (SQLException e)    {
            throw new RuntimeException(e);
        }
    }


    public static int getUnclassifiedId() throws SQLException {
        CommunityResource unclassified = CommunityResourceDAO.getCommunityResource("Unclassified");
         return unclassified.getId();

    }



}
