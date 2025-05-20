package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrintOrganizationsMain {

    public static void main(String[] args) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // First, read main organizations
            String sql = """
                    SELECT r.id, r.name, r.description, u.url
                    FROM community_resources r
                    LEFT JOIN resource_urls u ON r.id = u.resource_id AND u.url_type = 'website'
                    ORDER BY r.id
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int resourceId = rs.getInt("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String website = rs.getString("url");

                    printFormatted(name, description, website);

                    // Now fetch any suborganizations
                    printSubOrganizations(conn, resourceId);
                }
            }
        }
    }

    private static void printFormatted(String name, String description, String website) {
        System.out.println("Organization: " + (name != null ? name : "(unknown)"));
        if (description != null && !description.isBlank()) {
            System.out.println("  Description: " + description);
        }
        if (website != null && !website.isBlank()) {
            System.out.println("  Website: " + website);
        }
        System.out.println();
    }

    private static void printSubOrganizations(Connection conn, int parentId) throws SQLException {
        String sql = """
            SELECT name, description
            FROM sub_organizations
            WHERE parent_resource_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String description = rs.getString("description");

                    System.out.println("  Sub-Organization: " + (name != null ? name : "(unknown)"));
                    if (description != null && !description.isBlank()) {
                        System.out.println("    Description: " + description);
                    }
                    System.out.println();
                }
            }
        }
    }
}
