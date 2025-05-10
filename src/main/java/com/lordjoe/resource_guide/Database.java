package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.CategoryUtils;
import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {

    public static void clearDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Resetting database tables...");
             // old
            stmt.executeUpdate("DROP TABLE IF EXISTS resource_urls CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS sub_organizations CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS community_resource_properties CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS community_resource_sections CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS community_resources CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS category_descriptions CASCADE");
            // new
            stmt.executeUpdate("DROP TABLE IF EXISTS resource_sites CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS resource_descriptions CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS community_resources CASCADE");


            System.out.println("Tables dropped.");
            DatabaseConnection.clearConnection();
        }
    }

    public static void createDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create main table first
            stmt.executeUpdate("""
            CREATE TABLE community_resources (
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                type TEXT NOT NULL CHECK (type IN ('Category', 'Subcategory', 'Resource', 'Block')),
                parent_id INT REFERENCES community_resources(id)
            )
        """);

            // Now it's safe to insert the default category
            Catagory unclassified = CategoryUtils.CreateCatagory("Unclassified");

            stmt.executeUpdate("""
            CREATE TABLE resource_descriptions (
                id SERIAL PRIMARY KEY,
                resource_id INT NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                content TEXT NOT NULL,
                is_block BOOLEAN DEFAULT FALSE
            )
        """);

            stmt.executeUpdate("""
            CREATE TABLE resource_sites (
                id SERIAL PRIMARY KEY,
                resource_id INT NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                phone TEXT,         -- Multi-line or comma-separated
                email TEXT,
                website TEXT,       -- Comma-separated
                address TEXT,       -- Multi-line
                hours TEXT,
                notes TEXT
            )
        """);

            System.out.println("Tables created.");
            DatabaseConnection.clearConnection();
        }
    }



    public static void validateAllURLs() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, url FROM resource_urls WHERE is_validated = FALSE");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String url = rs.getString("url");

                boolean valid = com.lordjoe.resource_guide.util.URLValidator.isValidURL(url);

                try (PreparedStatement updatePs = conn.prepareStatement(
                        "UPDATE resource_urls SET is_validated = TRUE, is_valid = ? WHERE id = ?")) {
                    updatePs.setBoolean(1, valid);
                    updatePs.setInt(2, id);
                    updatePs.executeUpdate();
                }

                System.out.println("Validated URL: " + url + " - " + (valid ? "OK" : "FAILED"));
            }
        }
        DatabaseConnection.clearConnection();
    }
}
