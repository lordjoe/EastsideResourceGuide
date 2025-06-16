package com.lordjoe.sandhurst;

import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HouseLoader {


    public static List<House> loadAllHouses() {
        List<House> houses = new ArrayList<>();
        String sql = "SELECT id, latitude, longitude, address FROM house";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int id = 0;
            while (rs.next()) {
                try {
                     id = rs.getInt("id");
                    double lat = rs.getDouble("latitude");
                    double lon = rs.getDouble("longitude");
                    String address = rs.getString("address");
                    houses.add(new House(id, lat, lon, address));
                }
                catch (SQLException e) {
                    System.out.println("Bad House " + id );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load houses", e);
        }

        return houses;
    }
}
