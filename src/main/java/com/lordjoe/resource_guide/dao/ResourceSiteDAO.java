package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.model.ResourceSite;
import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceSiteDAO {

    public static void insert(ResourceSite site) throws SQLException {
        String sql = "INSERT INTO resource_sites (resource_id, address,   phone, email, website,hours, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ? )";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, site.getResourceId());
                ps.setString(2, site.getAddress());
                ps.setString(3, site.getPhone());
                ps.setString(4, site.getEmail());
                ps.setString(5, site.getWebsite());
                ps.setString(6, site.getHours());
                ps.setString(7, site.getNotes());
                ps.executeUpdate();
            }
        }
    }

    public static Map<Integer, ResourceSite> loadAllAsMap() throws SQLException {
        Map<Integer, ResourceSite> result = new HashMap<>();

        String sql = "SELECT * FROM resource_sites";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("resource_id");
                ResourceSite site = new ResourceSite(id);
                site.setAddress(rs.getString("address"));
                site.setPhone(rs.getString("phone"));
                site.setEmail(rs.getString("email"));
                site.setWebsite(rs.getString("website"));
                site.setHours(rs.getString("hours"));
                site.setNotes(rs.getString("notes"));

                result.put(id, site);
            }
        }
        return result;
    }


    public static List<ResourceSite> loadAll(Connection conn) throws SQLException {
        List<ResourceSite> result = new ArrayList<>();
        String sql = "SELECT * FROM resource_sites";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ResourceSite site = new ResourceSite(rs.getInt("resource_id"));
                site.setAddress(rs.getString("address"));
                site.setPhone(rs.getString("phone"));
                site.setEmail(rs.getString("email"));
                site.setWebsite(rs.getString("website"));
                site.setHours(rs.getString("hours"));
                site.setNotes(rs.getString("notes"));
                result.add(site);
            }
        }
        return result;
    }

    public static ResourceSite loadByResourceId(Connection conn, int resourceId) throws SQLException {
        String sql = "SELECT * FROM resource_sites WHERE resource_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resourceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResourceSite site = new ResourceSite(rs.getInt("resource_id"));
                    site.setAddress(rs.getString("address"));
                    site.setPhone(rs.getString("phone"));
                    site.setEmail(rs.getString("email"));
                    site.setWebsite(rs.getString("website"));
                    site.setHours(rs.getString("hours"));
                    site.setNotes(rs.getString("notes"));
                    return site;
                }
            }
        }
        return null;
    }
}
