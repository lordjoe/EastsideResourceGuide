package com.lordjoe.resource_guide;

/**
 * com.lordjoe.resource_guide.OrganizationPrinter
 * User: Steve
 * Date: 4/27/25
 */

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationPrinter {

    public static String fillOrganizationDetails(ResultSet rs, boolean isSubOrganization) throws SQLException {
        StringBuilder sb = new StringBuilder();

        // Determine whether it’s a main org or sub-org
        if (isSubOrganization) {
            sb.append("  [Sub-Organization]\n");
            sb.append("  Name: ").append(rs.getString("sub_name")).append("\n");
            sb.append("  Description: ").append(nullSafe(rs.getString("sub_description"))).append("\n");
            sb.append("  Website: ").append(nullSafe(rs.getString("sub_website"))).append("\n");
            sb.append("  Phone: ").append(nullSafe(rs.getString("sub_phone"))).append("\n");
        } else {
            sb.append("[Organization]\n");
            sb.append("Name: ").append(rs.getString("name")).append("\n");
            sb.append("Description: ").append(nullSafe(rs.getString("description"))).append("\n");
            sb.append("Website: ").append(nullSafe(rs.getString("website"))).append("\n");
            sb.append("Phone: ").append(nullSafe(rs.getString("phone_primary"))).append("\n");
        }

        sb.append("--------------------------------------\n");
        return sb.toString();
    }

    private static String nullSafe(String value) {
        return (value == null || value.isEmpty()) ? "(none)" : value;
    }
}