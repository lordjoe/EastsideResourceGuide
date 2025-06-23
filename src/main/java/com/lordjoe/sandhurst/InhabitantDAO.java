package com.lordjoe.sandhurst;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InhabitantDAO {

    private final Connection connection;

    public InhabitantDAO(Connection connection) {
        this.connection = connection;
    }

    public void loadAllInhabitants() throws SQLException {
        String sql = "SELECT id, resource_id, name, phone, email, type FROM inhabitant";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int resourceId = rs.getInt("resource_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String typeStr = rs.getString("type");

                InhabitantType type = InhabitantType.valueOf(typeStr);

                Inhabitant inhabitant = new Inhabitant(id, resourceId, name, phone, email, type);

                House house = Neighborhood.Instance.getHouse(resourceId);
                if (house != null) {
                    house.addInhabitant(inhabitant);
                } else {
                    System.err.println("No house found for resource_id: " + resourceId);
                }
            }
        }
    }
}
