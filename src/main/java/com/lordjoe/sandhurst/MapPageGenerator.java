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
        out.println("        const infoWindow = new google.maps.InfoWindow();");

        for (House house : Neighborhood.Instance.getHouses()) {
            int id = house.getId();
            String safeAddress = house.getAddress().replace("'", "\\'");
            double lat = house.getLatitude();
            double lon = house.getLongitude();

            out.printf("        const latlng%d = new google.maps.LatLng(%.7f, %.7f);%n", id, lat, lon);
            out.printf("        const marker%d = new google.maps.Marker({%n", id);
            out.printf("            position: latlng%d,%n", id);
            out.println("            map: map,");
            out.println("            icon: \"https://maps.google.com/mapfiles/ms/icons/purple-dot.png\"");
            out.println("        });");

            // Click navigates to house page
            out.printf("        marker%d.addListener('click', function() {%n", id);
            out.printf("            window.location.href = '/sandhurst/house/%d';%n", id);
            out.println("        });");

            // Hover shows bold address in info window
            out.printf("        marker%d.addListener('mouseover', function() {%n", id);
            out.printf("            infoWindow.setContent('<b>%s</b>');%n", safeAddress);
            out.printf("            infoWindow.open(map, marker%d);%n", id);
            out.println("        });");

            out.printf("        bounds.extend(latlng%d);%n", id);
        }

        out.println("        map.fitBounds(bounds);");

        // Hide the close (X) button when info window opens
        out.println("        google.maps.event.addListener(infoWindow, 'domready', function() {");
        out.println("            const iwCloseBtn = document.querySelector('.gm-ui-hover-effect');");
        out.println("            if (iwCloseBtn) iwCloseBtn.style.display = 'none';");
        out.println("        });");

        out.println("      }");
        out.println("      window.onload = initMap;");
        out.println("    </script>");
        out.println("    <style>");
        out.println("      html, body { height: 100%; margin: 0; padding: 0; }");
        out.println("      #map { height: 90vh; width: 100%; }");
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
