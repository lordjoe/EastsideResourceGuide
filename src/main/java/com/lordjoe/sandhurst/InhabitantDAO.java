
package com.lordjoe.sandhurst;

import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InhabitantDAO {

    private final Connection connection;

    public InhabitantDAO(Connection connection) {
        this.connection = connection;
    }

    public void loadAllInhabitants(Neighborhood instance) throws SQLException {
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

                House house = instance.getHouse(resourceId);
                if (house != null) {
                    house.addInhabitant(inhabitant);
                } else {
                    System.err.println("No house found for resource_id: " + resourceId);
                }
            }
        }
    }

    /**
     * Create a minimal Inhabitant in the DB, returning the new entity with generated ID.
     * This is used when the controller passes id=0 (new Inhabitant).
     */
    public Inhabitant createInhabitant(int houseId,String name,InhabitantType type) throws SQLException {
        String sql = "INSERT INTO inhabitant (house_id, name) VALUES (?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, houseId);
            ps.setString(2, name); // minimal placeholder
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    Inhabitant i = new Inhabitant(id,houseId,name,null,null,type);

                    return i;
                }
            }
        }
        throw new SQLException("Failed to insert Inhabitant");
    }

    /**
     * Deletes an inhabitant from the database by ID.
     *
     * @param id the inhabitant's primary key
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM inhabitant WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}
