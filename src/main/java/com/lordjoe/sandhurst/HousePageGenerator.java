package com.lordjoe.sandhurst;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HousePageGenerator {

    public static String generateHousePage(House house) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.printf("  <title>%s</title>%n", house.getAddress());
        out.println("</head>");
        out.println("<body>");
        out.printf("<h1>%s</h1>%n", house.getAddress());
        out.printf("<p>Latitude: %.6f</p>%n", house.getLatitude());
        out.printf("<p>Longitude: %.6f</p>%n", house.getLongitude());

        if (house.getImageUrl() != null && !house.getImageUrl().isEmpty()) {
            out.printf("<img src=\"%s\" alt=\"House photo\" style=\"max-width:400px;\"/>%n", house.getImageUrl());
        }

        out.println("<br><button onclick=\"location.href='/sandhurst'\">Sandhurst</button>");
        out.println("</body>");
        out.println("</html>");

        return writer.toString();
    }
}
