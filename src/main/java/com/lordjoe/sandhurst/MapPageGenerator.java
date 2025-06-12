package com.lordjoe.sandhurst;

import java.util.List;

public class MapPageGenerator {

    public static String generate(List<House> houses) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Neighborhood Map</title>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.3/dist/leaflet.css"/>
                <style>#map { height: 600px; }</style>
            </head>
            <body>
            <h2>Neighborhood Map</h2>
            <div id="map"></div>

            <script src="https://unpkg.com/leaflet@1.9.3/dist/leaflet.js"></script>
            <script>
                const map = L.map('map').setView([47.655, -122.201], 15);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; OpenStreetMap contributors'
                }).addTo(map);
        """);

        for (House house : houses) {
            if (house.getLatitude() != 0 && house.getLongitude() != 0) {
                String jsSafeAddress = house.getAddress().replace("'", "\\'");
                sb.append(String.format("""
                    L.marker([%f, %f]).addTo(map)
                        .bindPopup('%s');
                """, house.getLatitude(), house.getLongitude(), jsSafeAddress));
            }
        }

        sb.append("""
            </script>
            </body>
            </html>
        """);

        return sb.toString();
    }
}
