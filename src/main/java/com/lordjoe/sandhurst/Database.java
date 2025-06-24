package com.lordjoe.sandhurst;

import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.*;
import java.util.Map;

public class Database {

    public static void clearDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Resetting database tables...");
            // old
            stmt.executeUpdate("DROP TABLE IF EXISTS   Inhabitant");
            stmt.executeUpdate("DROP TABLE IF EXISTS   HOUSE");
            stmt.executeUpdate("DROP TABLE IF EXISTS   image_asset");


            System.out.println("Tables dropped.");
            DatabaseConnection.clearConnection();
        }
    }

    public static void createDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                    CREATE TABLE house (
                        id SERIAL PRIMARY KEY,
                        address TEXT NOT NULL,
                        latitude DOUBLE PRECISION,
                        longitude DOUBLE PRECISION
                    )
                    """);

            // Create the main table first
            stmt.executeUpdate("""
                        CREATE TABLE inhabitant (
                            id SERIAL PRIMARY KEY,
                            resource_id INT NOT NULL REFERENCES house(id) ON DELETE CASCADE,
                            name TEXT NOT NULL,
                            phone TEXT,
                            email TEXT,
                            type TEXT NOT NULL CHECK (type IN ('Adult', 'Child', 'Pet', 'Other'))
                        )
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE image_asset (
                            id SERIAL PRIMARY KEY,
                            source_id INT NOT NULL,
                            source_type TEXT NOT NULL CHECK (source_type IN ('house', 'inhabitant')),
                            image_url TEXT NOT NULL
                    ) 
                    """);

            System.out.println("Tables created.");
            DatabaseConnection.clearConnection();
        }
    }

    public static void loadHousesFromTSV(String filePath) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Use \COPY if from psql or load line by line
            System.out.println("Loading house data from " + filePath);

            try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File(filePath))) {
                if (scanner.hasNextLine()) scanner.nextLine(); // skip header
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println(line);
                    try {
                        String[] parts = line.split("\t");
                        if (parts.length == 3) {
                            String address = parts[0].replace("'", "''");
                            String lat = parts[1];
                            String lon = parts[2];
                            stmt.executeUpdate(String.format(
                                    "INSERT INTO house (address, latitude, longitude) VALUES ('%s', %s, %s)",
                                    address, lat, lon
                            ));
                        } else {
                            System.out.println("Invalid data line: " + line);
                        }
                    } catch (SQLException e) {
                        System.out.println("Invalid data line: " + line);
                        throw new RuntimeException(e);

                    }
                }
            }

            System.out.println("House data loaded.");
            DatabaseConnection.clearConnection();
        }
    }


    public static void loadInhabitantsFromTSV(String filePath) throws Exception {
        Map<String, Integer> addressMap = AddressUtils.buildNormalizedAddressMap(Neighborhood.Instance.getHouses());
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Loading inhabitant data from " + filePath);

            String sql = """
                        INSERT INTO inhabitant (resource_id, name, phone, email, type)
                        VALUES (?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 java.util.Scanner scanner = new java.util.Scanner(new java.io.File(filePath))) {

                if (scanner.hasNextLine()) scanner.nextLine(); // skip header

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.length() == 0) continue;
                    System.out.println(line);
                    try {
                        String[] parts = line.split("\t");
                        if (parts.length >= 2) {
                            String address = parts[0].trim();
                            String name = parts[1].trim();
                            String phone = parts.length > 2 ? parts[2].trim() : null;
                            String email = parts.length > 3 ? parts[3].trim() : null;

                            // Find house by address
                            Integer houseId = AddressUtils.findClosestHouseId(address, addressMap);

                            if (houseId != null) {
                                pstmt.setInt(1, houseId);
                                pstmt.setString(2, name);
                                pstmt.setString(3, phone);
                                pstmt.setString(4, email);
                                pstmt.setString(5, "Adult"); // defaulting to Adult
                                pstmt.executeUpdate();
                            } else {
                                System.out.println("No house found for address: " + address);
                            }

                        } else {
                            System.out.println("Invalid data line: " + line);
                        }
                    } catch (Exception e) {
                        System.out.println("Error processing line: " + line);
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Inhabitant data loaded.");
        } finally {
            DatabaseConnection.clearConnection();
        }
    }


    public void saveInhabitant(Inhabitant inhabitant) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {

            if (inhabitant.getId() == 0) {
                // INSERT new inhabitant
                String insertSQL = """
                            INSERT INTO inhabitant (resource_id, name, phone, email, type)
                            VALUES (?, ?, ?, ?, ?)
                            RETURNING id
                        """;
                try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                    stmt.setInt(1, inhabitant.getResourceId());
                    stmt.setString(2, inhabitant.getName());
                    stmt.setString(3, inhabitant.getPhone());
                    stmt.setString(4, inhabitant.getEmail());
                    stmt.setString(5, inhabitant.getType().name());

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        // You must reflect this new ID back into the object, if it's mutable
                        System.out.println("Inserted Inhabitant with id " + newId);
                    }
                }
            } else {
                // UPDATE existing inhabitant
                String updateSQL = """
                            UPDATE inhabitant
                            SET resource_id = ?, name = ?, phone = ?, email = ?, type = ?
                            WHERE id = ?
                        """;
                try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                    stmt.setInt(1, inhabitant.getResourceId());
                    stmt.setString(2, inhabitant.getName());
                    stmt.setString(3, inhabitant.getPhone());
                    stmt.setString(4, inhabitant.getEmail());
                    stmt.setString(5, inhabitant.getType().name());
                    stmt.setInt(6, inhabitant.getId());

                    stmt.executeUpdate();
                    System.out.println("Updated Inhabitant with id " + inhabitant.getId());
                }
            }
            DatabaseConnection.clearConnection();
        }
    }
}

