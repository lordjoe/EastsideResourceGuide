package com.lordjoe.resource_guide.dao;


import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static void guaranteeUser(String name, String encryptedPassword) {
        String selectSql = "SELECT password FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'USER')";
        String updateSql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, name);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    String existingPassword = rs.getString("password");
                    if (!existingPassword.equals(encryptedPassword)) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, encryptedPassword);
                            updateStmt.setString(2, name);
                            updateStmt.executeUpdate();
                        }
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, name);
                        insertStmt.setString(2, encryptedPassword);
                        insertStmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to guarantee user", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error in guaranteeUser", e);
        }
    }
}

