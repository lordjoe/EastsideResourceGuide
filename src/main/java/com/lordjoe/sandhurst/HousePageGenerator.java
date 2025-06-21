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

        out.println("""
                    <br>
                    <a href="/sandhurst" style="
                        display: inline-block;
                        padding: 8px 16px;
                        background-color: #4b0082;
                        color: white;
                        text-decoration: none;
                        border-radius: 6px;
                        font-weight: bold;
                        font-family: sans-serif;
                    ">Sandhurst</a>
                """);

        String imageUrl = HouseImageResolver.getImageUrl(house);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            out.printf("<img src=\"%s\" alt=\"House photo\" style=\"max-width:400px;\"/>%n", imageUrl);
        }

        out.println("""
                    <br>
                    <a href="/sandhurst" style="
                        display: inline-block;
                        padding: 8px 16px;
                        background-color: #4b0082;
                        color: white;
                        text-decoration: none;
                        border-radius: 6px;
                        font-weight: bold;
                        font-family: sans-serif;
                    ">Sandhurst</a>
                """);
        out.println("</body>");
        out.println("</html>");

        return writer.toString();
    }
}
