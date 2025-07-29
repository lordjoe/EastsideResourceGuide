package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.CategoryUtils;
import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.*;

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

    public static void createDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create main table first
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS community_resources (
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                type TEXT NOT NULL CHECK (type IN ('Category', 'Subcategory', 'Resource', 'Block')),
                parent_id INT REFERENCES community_resources(id)
            )
        """);

            // Insert default category if it doesn't exist
            if (CategoryUtils.getCategoryByName("Unclassified") == null) {
                CategoryUtils.CreateCatagory("Unclassified");
            }

            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS resource_descriptions (
                id SERIAL PRIMARY KEY,
                resource_id INT NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                content TEXT NOT NULL,
                is_block BOOLEAN DEFAULT FALSE
            )
        """);

            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS resource_sites (
                id SERIAL PRIMARY KEY,
                resource_id INT NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                phone TEXT,
                email TEXT,
                website TEXT,
                address TEXT,
                hours TEXT,
                notes TEXT
            )
        """);

            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'USER'
            )
        """);

            System.out.println("Tables created if they did not already exist.");
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
