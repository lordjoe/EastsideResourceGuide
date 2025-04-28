package com.lordjoe.resource_guide;

import com.lordjoe.viva.DBConnect;
import com.lordjoe.resource_guide.model.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * com.lordjoe.resource_guide.ResourceGuideHtmlGenerator
 * User: Steve
 * Date: 4/28/25
 */
public class ResourceGuideHtmlGenerator {

    public static void main(String[] args) throws Exception {
        generateHtml("resource_guide.html");
    }

    public static void generateHtml(String outputFilePath) throws Exception {
        try (PrintWriter out = new PrintWriter(new FileWriter(new File(outputFilePath)))) {
            out.println("<html>");
            out.println("<head><title>Community Resource Guide</title></head>");
            out.println("<body>");

            List<CommunityResource> resources = loadResources();

            for (CommunityResource res : resources) {
                out.println("<h2>" + escapeHtml(res.getName()) + "</h2>");
                if (res.getDescription() != null) {
                    out.println("<p>" + escapeHtml(res.getDescription()) + "</p>");
                }
                List<String> urls = loadUrls(res.getId(), null);
                for (String url : urls) {
                    out.println("<p><a href=\"" + url + "\">" + url + "</a></p>");
                }

                List<CommunityResource> subOrgs = loadSubOrganizations(res.getId());
                if (!subOrgs.isEmpty()) {
                    out.println("<ul>");
                    for (CommunityResource sub : subOrgs) {
                        out.println("<li><b>" + escapeHtml(sub.getName()) + "</b><br/>");
                        if (sub.getDescription() != null) {
                            out.println(escapeHtml(sub.getDescription()) + "<br/>");
                        }
                        List<String> subUrls = loadUrls(null, sub.getId());
                        for (String url : subUrls) {
                            out.println("<a href=\"" + url + "\">" + url + "</a><br/>");
                        }
                        out.println("</li>");
                    }
                    out.println("</ul>");
                }
            }

            out.println("</body>");
            out.println("</html>");
        }

        System.out.println("HTML file generated: " + outputFilePath);
    }

    private static List<CommunityResource> loadResources() throws Exception {
        List<CommunityResource> resources = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM community_resources ORDER BY category, subcategory, name");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CommunityResource resource = new CommunityResource();
                resource.setId(rs.getInt("id"));
                resource.setCategory(rs.getString("category"));
                resource.setSubcategory(rs.getString("subcategory"));
                resource.setName(rs.getString("name"));
                resource.setDescription(rs.getString("description"));
                resources.add(resource);
            }
        }

        return resources;
    }

    private static List<CommunityResource> loadSubOrganizations(int parentId) throws Exception {
        List<CommunityResource> subs = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM sub_organizations WHERE parent_resource_id = ?")) {
            ps.setInt(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommunityResource sub = new CommunityResource();
                    sub.setId(rs.getInt("id"));
                    sub.setName(rs.getString("name"));
                    sub.setDescription(rs.getString("description"));
                    subs.add(sub);
                }
            }
        }

        return subs;
    }

    private static List<String> loadUrls(Integer resourceId, Integer subOrgId) throws Exception {
        List<String> urls = new ArrayList<>();

        String sql = "SELECT url FROM resource_urls WHERE ";
        if (resourceId != null) {
            sql += "resource_id = ?";
        } else if (subOrgId != null) {
            sql += "suborganization_id = ?";
        } else {
            return urls;
        }

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (resourceId != null) {
                ps.setInt(1, resourceId);
            } else {
                ps.setInt(1, subOrgId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    urls.add(rs.getString("url"));
                }
            }
        }

        return urls;
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
