package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.SubOrganization;
import com.lordjoe.viva.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static com.lordjoe.resource_guide.util.StringUtils.truncate;
/**
 * com.lordjoe.resource_guide.SubOrganizationDAO
 * User: Steve
 * Date: 4/27/25
 */
public class SubOrganizationDAO {

    public static void insert(SubOrganization subOrg) throws SQLException {
        String sql = """
            INSERT INTO sub_organizations (
                parent_resource_id,
                name,
                description,
                address_line1,
                address_line2,
                city,
                state,
                zip_code,
                phone_primary,
                phone_secondary,
                email,
                website,
                notes
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subOrg.getParentResourceId());
            ps.setString(2, truncate(subOrg.getName(), 500));
            ps.setString(3, subOrg.getDescription()); // no truncation
            ps.setString(4, truncate(subOrg.getAddressLine1(), 500));
            ps.setString(5, truncate(subOrg.getAddressLine2(), 500));
            ps.setString(6, truncate(subOrg.getCity(), 255));
            ps.setString(7, truncate(subOrg.getState(), 50));
            ps.setString(8, truncate(subOrg.getZipCode(), 20));
            ps.setString(9, truncate(subOrg.getPhonePrimary(), 50));
            ps.setString(10, truncate(subOrg.getPhoneSecondary(), 50));
            ps.setString(11, truncate(subOrg.getEmail(), 255));
            ps.setString(12, truncate(subOrg.getWebsite(), 2000));
            ps.setString(13, subOrg.getNotes()); // assume TEXT

            ps.executeUpdate();
        }
    }
}
