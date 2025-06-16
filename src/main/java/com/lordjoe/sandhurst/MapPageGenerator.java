package com.lordjoe.sandhurst;

import java.io.PrintWriter;

public class MapPageGenerator {

    public static void writeMapPage(PrintWriter out, String mapApiKey) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("    <title>Sandhurst Neighborhood Map</title>");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        out.println("    <script src=\"https://maps.googleapis.com/maps/api/js?key=" + mapApiKey + "\"></script>");
        out.println("    <script>");
        out.println("      function initMap() {");
        out.println("        const map = new google.maps.Map(document.getElementById('map'), {});");
        out.println("        const bounds = new google.maps.LatLngBounds();");

        for (House house : Neighborhood.Instance.getHouses()) {
            int id = house.getId();
            double lat = house.getLatitude();
            double lon = house.getLongitude();

            out.printf("        const marker%d = new google.maps.Marker({ position: { lat: %.7f, lng: %.7f }, map: map });%n", id, lat, lon);
            out.printf("        marker%d.addListener('click', function() { window.location.href = '/sandhurst/house/%d'; });%n", id, id);
            out.printf("        bounds.extend(marker%d.getPosition());%n", id);
        }

        out.println("        map.fitBounds(bounds);");
        out.println("      }");
        out.println("      window.onload = initMap;");
        out.println("    </script>");
        out.println("    <style>");
        out.println("      html, body { height: 100%%; margin: 0; padding: 0; }");
        out.println("      #map { height: 90vh; width: 100%%; }");
        out.println("      #topbar { padding: 10px; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("  <div id=\"topbar\">");
        out.println("    <button onclick=\"location.href='/sandhurst'\">Sandhurst</button>");
        out.println("  </div>");
        out.println("  <div id=\"map\"></div>");
        out.println("</body>");
        out.println("</html>");
    }
}
