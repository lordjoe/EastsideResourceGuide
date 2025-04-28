package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.model.ResourceUrl;
import com.lordjoe.viva.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResourceUrlDAO {

    public static void insert(ResourceUrl url) throws SQLException {
        String sql = """
            INSERT INTO resource_urls (resource_id, url_type, url, is_validated, is_valid, last_checked, notes)
            VALUES (?, ?, ?, FALSE, NULL, NULL, NULL)
        """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, url.getResourceId());
            ps.setString(2, url.getUrlType());
            ps.setString(3, url.getUrl());

            ps.executeUpdate();
        }
    }
}
