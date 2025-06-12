package com.lordjoe.sandhurst;

import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    public static void clearDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Resetting database tables...");
            // old
            stmt.executeUpdate("DROP TABLE IF EXISTS   HOUSE");
 


            System.out.println("Tables dropped.");
            DatabaseConnection.clearConnection();
        }
    }

    public static void createDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create the main table first
            stmt.executeUpdate("""
                    CREATE TABLE house (
                        id SERIAL PRIMARY KEY,
                        address TEXT NOT NULL,
                        latitude DOUBLE PRECISION,
                        longitude DOUBLE PRECISION
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
                        }
                        else {
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
 }
