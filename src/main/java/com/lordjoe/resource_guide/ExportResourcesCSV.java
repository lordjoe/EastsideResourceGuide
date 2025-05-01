package com.lordjoe.resource_guide;

/**
 * com.lordjoe.resource_guide.ExportResourcesCSV
 * User: Steve
 * Date: 4/27/25
 */


import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.io.FileWriter;
import java.sql.*;

public class ExportResourcesCSV {
    public static void main(String[] args) throws Exception {
        String categoryFilter = null; // e.g., "Transportation"
        String subcategoryFilter = null; // e.g., "Vehicle Licensing"
        String subSubcategoryFilter = null; // e.g., "State-Contracted Sub-Agent Offices"

        exportResources("resources_export.csv", categoryFilter, subcategoryFilter, subSubcategoryFilter);
    }

    public static void exportResources(String fileName, String category, String subcategory, String subSubcategory) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT category, subcategory, sub_subcategory, name, description, " +
                " (SELECT url FROM resource_urls WHERE resource_id = r.id LIMIT 1) as website " +
                " FROM community_resources r WHERE 1=1 ");

        if (category != null) sql.append("AND category = '").append(category.replace("'", "''")).append("' ");
        if (subcategory != null) sql.append("AND subcategory = '").append(subcategory.replace("'", "''")).append("' ");
        if (subSubcategory != null) sql.append("AND sub_subcategory = '").append(subSubcategory.replace("'", "''")).append("' ");

        sql.append("ORDER BY category, subcategory, sub_subcategory, name");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString());
             FileWriter writer = new FileWriter(fileName)) {

            writer.write("Category,Subcategory,SubSubcategory,Name,Description,Website\n");

            while (rs.next()) {
                writer.write(quote(rs.getString("category")) + ",");
                writer.write(quote(rs.getString("subcategory")) + ",");
                writer.write(quote(rs.getString("sub_subcategory")) + ",");
                writer.write(quote(rs.getString("name")) + ",");
                writer.write(quote(rs.getString("description")) + ",");
                writer.write(quote(rs.getString("website")) + "\n");
            }
        }

        System.out.println("CSV export complete: " + fileName);
    }

    private static String quote(String s) {
        if (s == null) return "\"\"";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
