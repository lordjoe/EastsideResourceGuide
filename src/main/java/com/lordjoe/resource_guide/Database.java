package com.lordjoe.resource_guide;

import com.lordjoe.viva.DBConnect;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    public static void clearDatabase() throws Exception {
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Resetting database tables...");

            stmt.executeUpdate("DROP TABLE IF EXISTS resource_urls CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS sub_organizations CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS community_resource_properties CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS community_resource_sections CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS community_resources CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS category_descriptions CASCADE");

            System.out.println("Tables dropped.");
        }
    }

    public static void createDatabase() throws Exception {
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Creating tables...");

            // Main resources
            stmt.executeUpdate("""
                        CREATE TABLE community_resources (
                            id SERIAL PRIMARY KEY,
                            category VARCHAR(255),
                            subcategory VARCHAR(255),
                            name VARCHAR(500) NOT NULL,
                            description TEXT,
                            address_line1 VARCHAR(500),
                            address_line2 VARCHAR(500),
                            city VARCHAR(255),
                            state VARCHAR(50),
                            zip_code VARCHAR(20),
                            phone_primary VARCHAR(50),
                            phone_secondary VARCHAR(50),
                            email VARCHAR(255),
                            hours TEXT,
                            notes TEXT
                        )
                    """);

            // Sub-organizations
            stmt.executeUpdate("""
                        CREATE TABLE sub_organizations (
                            id SERIAL PRIMARY KEY,
                            parent_resource_id INTEGER NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                            name VARCHAR(500) NOT NULL,
                            description TEXT,
                            address_line1 VARCHAR(500),
                            address_line2 VARCHAR(500),
                            city VARCHAR(255),
                            state VARCHAR(50),
                            zip_code VARCHAR(20),
                            phone_primary VARCHAR(50),
                            phone_secondary VARCHAR(50),
                            email VARCHAR(255),
                            hours TEXT,
                            notes TEXT
                        )
                    """);

            // Resource URLs
            stmt.executeUpdate("""
                        CREATE TABLE resource_urls (
                            id SERIAL PRIMARY KEY,
                            resource_id INTEGER REFERENCES community_resources(id) ON DELETE CASCADE,
                            suborganization_id INTEGER REFERENCES sub_organizations(id) ON DELETE CASCADE,
                            url_type VARCHAR(255),
                            url TEXT NOT NULL,
                            is_validated BOOLEAN DEFAULT FALSE,
                            is_valid BOOLEAN,
                            last_checked TIMESTAMP,
                            notes TEXT
                        )
                    """);

            // Extra properties
            stmt.executeUpdate("""
                        CREATE TABLE community_resource_properties (
                            id SERIAL PRIMARY KEY,
                            resource_id INTEGER NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                            property_name VARCHAR(255) NOT NULL,
                            property_value TEXT
                        )
                    """);

            // Section mapping
            stmt.executeUpdate("""
                        CREATE TABLE community_resource_sections (
                            id SERIAL PRIMARY KEY,
                            section_name VARCHAR(255) NOT NULL,
                            property_name VARCHAR(255) NOT NULL
                        )
                    """);

            // category_descriptions
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS category_descriptions (
                            id SERIAL PRIMARY KEY,
                            category_name VARCHAR(255) NOT NULL,
                            subcategory_name VARCHAR(255),
                            description TEXT
                        )
                    """);
            System.out.println("Tables created.");
        }
    }

    public static void validateAllURLs() throws Exception {
        try (Connection conn = DBConnect.getConnection();
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
    }
}
