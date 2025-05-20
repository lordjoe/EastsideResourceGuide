package com.lordjoe.viva;

import com.lordjoe.resource_guide.util.DatabaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GetOrganizationsServlet", urlPatterns = "/getOrganizations")
public class GetOrganizationsServlet extends HttpServlet {

    private static class Organization {
          String name;

        Organization(  String name) {
              this.name = name;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();

        try {
             Connection connection = DatabaseConnection.getConnection();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT   name FROM organizations");

            List<Organization> orgs = new ArrayList<>();
            while (rs.next()) {
                orgs.add(new Organization( rs.getString("name")));
            }

            // Write the HTML directly
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<title>Delete Organization</title>");
            writer.println("<style>");
            writer.println(".container { max-width: 600px; margin: 20px auto; padding: 20px; }");
            writer.println(".form-group { margin-bottom: 20px; }");
            writer.println("select { width: 100%; padding: 8px; margin-bottom: 20px; }");
            writer.println(".button-group { display: flex; gap: 10px; }");
            writer.println(".button-group button { padding: 10px 20px; }");
            writer.println(".delete-btn { background-color: #ff4444; color: white; border: none; cursor: pointer; }");
            writer.println(".cancel-btn { background-color: #888; color: white; border: none; cursor: pointer; }");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<div class='container'>");
            writer.println("<h1>Delete Organization</h1>");
            writer.println("<form id='deleteForm' action='deleteOrganization' method='POST'>");
            writer.println("<div class='form-group'>");
            writer.println("<label for='organizationId'>Select Organization to Delete:</label>");
            writer.println("<select id='organizationId' name='organizationId' required>");
            writer.println("<option value=''>-- Select an organization --</option>");

            for (Organization org : orgs) {
                String nm = escapeHtml(org.name);
                writer.printf("<option value='%d'>%s</option>%n",nm);

            }

            writer.println("</select>");
            writer.println("</div>");
            writer.println("<div class='button-group'>");
            writer.println("<button type='submit' class='delete-btn' onclick='return confirm(\"Are you sure you want to delete this organization?\")'>Delete</button>");
            writer.println("<button type='button' class='cancel-btn' onclick='window.location.href=\"organizations\"'>Cancel</button>");
            writer.println("</div>");
            writer.println("</form>");
            writer.println("</div>");
            writer.println("</body>");
            writer.println("</html>");

        } catch (Exception e) {
            writer.println("Error: " + escapeHtml(e.getMessage()));
        }
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}