package com.lordjoe.viva;

/**
 * com.lordjoe.viva.OrganizationServlet
 * User: Steve
 * Date: 4/21/25
 */

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet(name = "OrganizationServlet", urlPatterns = "/organizations")
public class OrganizationServlet extends HttpServlet {

    public static final String[] columns = {
            "name",
            "website",
            "volunteerPage",
            "contactName",
            "address",
            "phone",
            "logo",
            "allowsCourtOrdered",
            "filters",
      };

    private void writeOrganization(PrintWriter writer,ResultSet lresultSet) throws SQLException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            sb.append (getColumn(lresultSet,column) );
            if(i < columns.length - 1)
                sb.append ("\t");
            else
                writer.println ( sb.toString());
        }
    }

    private String getColumn(ResultSet rs,String column) {
        try {
            return rs.getString(column);
        }
        catch (SQLException e) {
            return "";
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws  IOException {
        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        try {
            Connection connection = DBConnect.getConnection();
            final Statement lstatement = connection.createStatement();
            final ResultSet lresultSet = lstatement.executeQuery("SELECT * from organizations");
            while (lresultSet.next()) {
                writeOrganization(writer,lresultSet);
             }

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
     }
}

