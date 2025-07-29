package com.lordjoe.resource_guide.util;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * com.lordjoe.resource_guide.util.DeleteDuplicates 
 * User: Steve
 * Date: 7/28/25
 */
public class DeleteDuplicates {

static List<Integer> idsToDelete = List.of(19,20,21,45,64,76,79,
        104,115,137,138,149,159,181,189,190,204,241,302,328,344,360,373,406,408,441);
   static String idList = idsToDelete.stream().map(String::valueOf).collect(Collectors.joining(","));

    public static void main(String[] args)  throws Exception {
        String deleteDescriptions = "DELETE FROM resource_descriptions WHERE resource_id IN (" + idList + ")";
        String deleteSites = "DELETE FROM resource_sites WHERE resource_id IN (" + idList + ")";
        String deleteResources = "DELETE FROM community_resources WHERE id IN (" + idList + ")";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteDescriptions);
                stmt.executeUpdate(deleteSites);
                stmt.executeUpdate(deleteResources);
            }
        }
    }
}
