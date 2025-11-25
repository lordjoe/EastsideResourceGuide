package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import com.lordjoe.resource_guide.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityResourceDAO {
    public static CommunityResource create(int id, String name, ResourceType type, Integer parentId) {
 
            return create(id, name, type, parentId, null);

    }

    public static CommunityResource create(int id, String name, ResourceType type, Integer parentId, String description) {
        try {
            if (id != 0) {
                CommunityResource ret = CommunityResource.getInstance(id);
                ret.setName(name);
                ret.setDescription(description);
                ret.setType(type);
                ret.setParentId(parentId);
                return ret;
            }
            try (Connection conn = DatabaseConnection.getConnection()) {
              //  name = "Rename Me";
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO community_resources (name, type, parent_id) VALUES (?, ?, ?) RETURNING id");
                stmt.setString(1, name);
                String string = type.toString();
                stmt.setString(2, string);
                stmt.setInt(3, parentId);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    DatabaseConnection.clearConnection();
                    int newId = rs.getInt("id");
                    if (description != null && description.length() > 0) {
                        String descSql = "UPDATE resource_descriptions SET content = ? WHERE resource_id = ? AND is_block = FALSE";
                        try (PreparedStatement stmt2 = conn.prepareStatement(descSql)) {
                            stmt.setString(1, description);
                            stmt.setInt(2, newId);
                            stmt.executeUpdate();
                        }
                    }
                    CommunityResource ret = CommunityResource.getResource(newId,name,type,parentId);
                    ret.setDescription(description);
                    ret.setParentId(parentId);
                    ret.setName(name);
                    ret.setType(type);
                    return ret;

                }

                throw new SQLException("Insert failed, no ID obtained.");
                // keep connection  open
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int insert(CommunityResource resource) throws SQLException {
        int ret = 0;
        if (resource.getId() != 0) {
            update(resource);
            return resource.getId();
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO community_resources (name, type, parent_id) VALUES (?, ?, ?) RETURNING id");
            stmt.setString(1, resource.getName());
            String string = resource.getType().toString();
            stmt.setString(2, string);
            if (resource.getParentId() != null) {
                stmt.setInt(3, resource.getParentId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DatabaseConnection.clearConnection();
                ret = rs.getInt("id");
                String description = resource.getDescription();
                if (description != null && description.length() > 0) {
                    String descSql = "UPDATE resource_descriptions SET content = ? WHERE resource_id = ? AND is_block = FALSE";
                    try (PreparedStatement stmt2 = conn.prepareStatement(descSql)) {
                        stmt.setString(1, description);
                        stmt.setInt(2, ret);
                        stmt.executeUpdate();
                    }
                }
            }
            throw new SQLException("Insert failed, no ID obtained.");
            // keep connection  open
        }
    }


    public static int insert(ResourceDescription resource) throws SQLException {
        int ret = 0;
        if (resource.getResourceId() != 0) {
            return resource.getResourceId();
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO community_resources (name, type, parent_id) VALUES (?, ?, ?) RETURNING id");
            String string = ResourceType.Block.toString();
            stmt.setString(2, string);
            stmt.setInt(3, resource.getParentId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DatabaseConnection.clearConnection();
                ret = rs.getInt("id");
                String description = resource.getDescription();
                if (description != null && description.length() > 0) {
                    String descSql = "UPDATE resource_descriptions SET content = ? WHERE resource_id = ? AND is_block = TRUE";
                    try (PreparedStatement stmt2 = conn.prepareStatement(descSql)) {
                        stmt.setString(1, description);
                        stmt.setInt(2, ret);
                        stmt.executeUpdate();
                    }
                }
            }
            throw new SQLException("Insert failed, no ID obtained.");
            // keep connection  open
        }
    }


    public static void update(CommunityResource resource) {
        try {
            String sql = "UPDATE community_resources SET name = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, resource.getName());
                stmt.setInt(2, resource.getId());
                stmt.executeUpdate();
            }

            String siteSql = "UPDATE resource_sites SET phone  = ?, email = ?, website = ?, " +
                    "address  = ?, hours = ?, notes = ? WHERE resource_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(siteSql)) {
                stmt.setString(1, resource.getPhone());
                stmt.setString(2, resource.getEmail());
                stmt.setString(3, resource.getWebsite());
                stmt.setString(4, resource.getAddress());
                stmt.setString(5, resource.getHours());
                stmt.setString(6, resource.getNotes());
                stmt.setInt(7, resource.getId());
                stmt.executeUpdate();
            }

            String description = resource.getDescription();
            if (description != null && description != "") {
                String descSql = "UPDATE resource_descriptions SET content = ? WHERE resource_id = ? AND is_block = FALSE";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(descSql)) {
                    stmt.setString(1, description);
                    stmt.setInt(2, resource.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }


    public static void delete(int resourceId) {
        CommunityResource instance = CommunityResource.getInstance(resourceId);
        if(instance != null)  {
            CommunityResource.dropInstance(instance);
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Delete from resource_descriptions (ON DELETE CASCADE should help, but ensure cleanup)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM resource_descriptions WHERE resource_id = ?")) {
                stmt.setInt(1, resourceId);
                int affected = stmt.executeUpdate();
                if (affected < 0) {
                    throw new SQLException("Failed to delete resource_descriptions for resource_id = " + resourceId);
                }
            }

            // Delete from resource_sites
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM resource_sites WHERE resource_id = ?")) {
                stmt.setInt(1, resourceId);
                stmt.executeUpdate();
            }

            // Delete from community_resources
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM community_resources WHERE id = ?")) {
                stmt.setInt(1, resourceId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting resource ID " + resourceId, e);
        }
    }


    public static Map<Integer, CommunityResource> loadAllAsMap() throws SQLException {
        Map<Integer, CommunityResource> result = new HashMap<>();

        String sql = "SELECT * FROM community_resources";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                ResourceType type = ResourceType.Resource.valueOf(rs.getString("type"));
                String name = rs.getString("name");
                Integer parentId = rs.getInt("parent_id");

                CommunityResource resource = CommunityResource.getResource(id, name,
                        type, parentId);

                result.put(resource.getId(), resource);
            }
        }
        return result;
    }


    public static CommunityResource getCommunityResource(String name) throws SQLException {

        String sql = "SELECT * FROM community_resources where name = " + "name ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                ResourceType type = ResourceType.Resource.valueOf(rs.getString("type"));
                String namex = rs.getString("name");
                Integer parentId = rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null;
                CommunityResource resource = CommunityResourceDAO.create(id, namex,
                        type, parentId);

                return resource;
            }
            return null;
        }
    }


    public static List<CommunityResource> loadAll() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM community_resources");
        ResultSet rs = stmt.executeQuery();
        List<CommunityResource> results = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            ResourceType type = ResourceType.Resource.valueOf(rs.getString("type"));
            String namex = rs.getString("name");
            Integer parentId = rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null;
            CommunityResource cr = CommunityResourceDAO.create(id, namex,
                    type, parentId);
            results.add(cr);
        }
        return results;
    }

    public static void deleteResourceAndDependents(int resourceId) {
        String deleteDescriptions = "DELETE FROM resource_descriptions WHERE resource_id = ?";
        String deleteSites        = "DELETE FROM resource_sites        WHERE resource_id = ?";
        String deleteResource     = "DELETE FROM community_resources    WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(deleteDescriptions)) {
                    ps.setInt(1, resourceId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(deleteSites)) {
                    ps.setInt(1, resourceId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(deleteResource)) {
                    ps.setInt(1, resourceId);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed deleting resource " + resourceId + " and dependents", e);
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error deleting resource " + resourceId, e);
        }
    }

    public static void deleteAll() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM community_resources");
        stmt.executeUpdate();
    }

    public static void deleteResourceCascade(int resourceId) {
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM resource_sites WHERE resource_id = ?")) {
                ps.setInt(1, resourceId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM resource_descriptions WHERE resource_id = ?")) {
                ps.setInt(1, resourceId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM community_resources WHERE id = ?")) {
                ps.setInt(1, resourceId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cascade delete failed for resource id=" + resourceId, e);
        }
    }


}