package com.lordjoe.resource_guide;

/**
 * com.lordjoe.resource_guide.PrintResourcesMain
 * User: Steve
 * Date: 4/27/25
 */

import com.lordjoe.viva.DBConnect;

import java.sql.*;

public class PrintResourcesMain {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT category, subcategory,   name, description, " +
                    "       (SELECT url FROM resource_urls WHERE resource_id = r.id LIMIT 1) as website " +
                    "FROM community_resources r ORDER BY category, subcategory, sub_subcategory, name";

            try (ResultSet rs = stmt.executeQuery(sql)) {
                String lastCategory = "";
                String lastSubcategory = "";
                String lastSubSubcategory = "";

                while (rs.next()) {
                    String category = rs.getString("category");
                    String subcategory = rs.getString("subcategory");
                    String subSubcategory = rs.getString("sub_subcategory");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String website = rs.getString("website");

                    if (!category.equals(lastCategory)) {
                        System.out.println("\n=== Category: " + category + " ===");
                        lastCategory = category;
                        lastSubcategory = "";
                        lastSubSubcategory = "";
                    }
                    if (subcategory != null && !subcategory.equals(lastSubcategory)) {
                        System.out.println("\n--- Subcategory: " + subcategory + " ---");
                        lastSubcategory = subcategory;
                        lastSubSubcategory = "";
                    }
                    if (subSubcategory != null && !subSubcategory.equals(lastSubSubcategory)) {
                        System.out.println("\n> Sub-Subcategory: " + subSubcategory);
                        lastSubSubcategory = subSubcategory;
                    }

                    System.out.println("• Name: " + name);
                    if (description != null && !description.isBlank()) {
                        System.out.println("  Description: " + description);
                    }
                    if (website != null && !website.isBlank()) {
                        System.out.println("  Website: " + website);
                    }
                }
            }
        }
    }
}
