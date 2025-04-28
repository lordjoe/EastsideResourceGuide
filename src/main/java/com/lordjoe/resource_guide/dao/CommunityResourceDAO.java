package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.viva.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.lordjoe.resource_guide.util.StringUtils.truncate;
public class CommunityResourceDAO {


    public static List<CommunityResource> loadAllResources() throws SQLException {
        List<CommunityResource> resources = new ArrayList<>();

        String sql = "SELECT cr.id, cr.category, cr.subcategory, cr.name, cr.description, " +
                "cr.address_line1, cr.phone_primary, cr.email, cr.hours, ru.url AS website " +
                "FROM community_resources cr " +
                "LEFT JOIN resource_urls ru ON cr.id = ru.resource_id AND ru.url_type = 'main' " +
                "ORDER BY cr.category, cr.subcategory, cr.name";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CommunityResource resource = new CommunityResource();
                resource.setId(rs.getInt("id"));
                resource.setCategory(rs.getString("category"));
                resource.setSubcategory(rs.getString("subcategory"));
                resource.setName(rs.getString("name"));
                resource.setDescription(rs.getString("description"));
                resource.setAddressLine1(rs.getString("address_line1"));
                resource.setPhonePrimary(rs.getString("phone_primary"));
                resource.setEmail(rs.getString("email"));
                resource.setHours(rs.getString("hours"));
                resource.setWebsite(rs.getString("website"));  // <-- NEW

                resources.add(resource);
            }
        }

        return resources;
    }

    public static int insert(CommunityResource resource) throws SQLException {
        String sql = """
        INSERT INTO community_resources 
        (category, subcategory, name, description, address_line1, address_line2, city, state, zip_code, phone_primary, phone_secondary, email, hours, notes) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
        RETURNING id
    """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, truncate(resource.getCategory(), 255));
            ps.setString(2, truncate(resource.getSubcategory(), 255));
            ps.setString(3, truncate(resource.getName(), 500));
            ps.setString(4, resource.getDescription());
            ps.setString(5, truncate(resource.getAddressLine1(), 500));
            ps.setString(6, truncate(resource.getAddressLine2(), 500));
            ps.setString(7, truncate(resource.getCity(), 255));
            ps.setString(8, truncate(resource.getState(), 50));
            ps.setString(9, truncate(resource.getZipCode(), 20));
            ps.setString(10, truncate(resource.getPhonePrimary(), 50));
            ps.setString(11, truncate(resource.getPhoneSecondary(), 50));
            ps.setString(12, truncate(resource.getEmail(), 255));
            ps.setString(13, resource.getHours());
            ps.setString(14, resource.getNotes());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    resource.setId(id);
                    return id;
                } else {
                    throw new SQLException("Failed to insert community resource, no ID returned.");
                }
            }
        }
    }
}
