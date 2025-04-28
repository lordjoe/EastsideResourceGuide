package com.lordjoe.viva;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



/**
 * com.lordjoe.viva.AddOrganizationServlet
 * User: Steve
 * Date: 4/22/25
 */
@WebServlet(name = "AddOrganizationServlet", urlPatterns = "/addOrganization")
public class AddOrganizationServlet extends HttpServlet {


    private static class UrlValidationResult {
        final boolean isValid;
        final String message;

        UrlValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
    }

    private UrlValidationResult validateUrl(String urlString, String fieldName) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set timeouts to avoid hanging
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            // Some sites block default User-Agent, so we'll set a common browser one
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();

            // Check if response code is in the successful range (200-299)
            if (responseCode >= 200 && responseCode < 300) {
                return new UrlValidationResult(true, "");
            } else {
                return new UrlValidationResult(false,
                        fieldName + " URL returned error code: " + responseCode);
            }

        } catch (Exception e) {
            return new UrlValidationResult(false,
                    fieldName + " URL is not accessible: " + e.getMessage());
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String logo = req.getParameter("logo");
        String website = req.getParameter("website");
        String volunteerPage = req.getParameter("volunteerPage");
        String name = req.getParameter("name");

        // Collect all validation errors
        List<String> validationErrors = new ArrayList<>();

        // Validate all URLs
        UrlValidationResult logoValidation = validateUrl(logo, "Logo");
        if (!logoValidation.isValid) {
            validationErrors.add(logoValidation.message);
        }

        UrlValidationResult websiteValidation = validateUrl(website, "Website");
        if (!websiteValidation.isValid) {
            validationErrors.add(websiteValidation.message);
        }

        UrlValidationResult volunteerValidation = validateUrl(volunteerPage, "Volunteer Page");
        if (!volunteerValidation.isValid) {
            validationErrors.add(volunteerValidation.message);
        }

        // If there are validation errors, return them to the user
        if (!validationErrors.isEmpty()) {
            resp.setContentType("text/html");
            PrintWriter writer = resp.getWriter();
            writer.println("<html><body>");
            writer.println("<h2>Validation Errors:</h2>");
            writer.println("<ul>");
            for (String error : validationErrors) {
                writer.println("<li>" + error + "</li>");
            }
            writer.println("</ul>");
            writer.println("<a href='javascript:history.back()'>Go Back</a>");
            writer.println("</body></html>");
            return;
        }

        // If all validations pass, proceed with database insertion
        try {
             Connection connection = DBConnect.getConnection();
            Statement statement = connection.createStatement();
            ResultSet test = statement.executeQuery("SELECT * from organizations where name=" + name);
            if(test.isBeforeFirst()) {
                String sql = "INSERT INTO organizations (name, logo, website, volunteerPage) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, logo);
                stmt.setString(3, website);
                stmt.setString(4, volunteerPage);

                stmt.executeUpdate();
            }

            resp.sendRedirect("organizations");

        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}



