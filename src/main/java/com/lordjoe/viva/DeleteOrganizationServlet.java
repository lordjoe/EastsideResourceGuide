package com.lordjoe.viva;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet(name = "DeleteOrganizationServlet", urlPatterns = "/deleteOrganization")
public class DeleteOrganizationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String organizationId = req.getParameter("organizationId");

        if (organizationId == null || organizationId.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Organization ID is required");
            return;
        }

        try {
              Connection connection = DBConnect.getConnection();

            String sql = "DELETE FROM organizations WHERE name = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(organizationId));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                resp.sendRedirect("organizations");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Organization not found");
            }

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error deleting organization: " + e.getMessage());
        }
    }
}