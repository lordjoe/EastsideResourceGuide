package com.lordjoe.sandhurst;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class HousePageMaker {

    public static String generate(House house, HttpServletRequest request) {
        String apiKey = MapsKeyFetcher.getMapsApiKey();
        boolean isAuthorized = isUserAuthorized(request);

        StringBuilder html = new StringBuilder();
        html.append("<html>\n")
                .append("<head>\n")
                .append("<title>").append(house.getAddress()).append("</title>\n")
                .append("</head>\n")
                .append("<body>\n");

        html.append("""
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

        html.append("<h1>").append(house.getAddress()).append("</h1>\n");

        // Street View Image
        html.append("<img src=\"https://maps.googleapis.com/maps/api/streetview?size=600x400")
                .append("&location=").append(house.getLatitude()).append(",").append(house.getLongitude())
                .append("&key=").append(apiKey).append("\" alt=\"House Image\" /><br/><br/>\n");

        // Inhabitants
        html.append("<h3>Inhabitants</h3>\n<ul>\n");
        for (Inhabitant inh : house.getInhabitants()) {
            String type = inh.getType() == InhabitantType.Adult ? "Owner" : inh.getType().name();
            html.append("<li>")
                    .append("<strong>").append(inh.getName()).append("</strong>");
            if (inh.getPhone() != null && !inh.getPhone().isEmpty())
                html.append(" | ").append(inh.getPhone());
            if (inh.getEmail() != null && !inh.getEmail().isEmpty())
                html.append(" | <a href=\"mailto:").append(inh.getEmail()).append("\">").append(inh.getEmail()).append("</a>");
            html.append(" (").append(type).append(")");
            html.append("</li>\n");
        }
        html.append("</ul>\n");

        // Add Inhabitant button
        if (isAuthorized) {
            html.append("<form action=\"/sandhurst/addInhabitant\" method=\"GET\">\n")
                    .append("<input type=\"hidden\" name=\"houseId\" value=\"").append(house.getId()).append("\"/>\n")
                    .append("<button type=\"submit\">Add Inhabitant</button>\n")
                    .append("</form>\n");
        }

        html.append("</body>\n</html>\n");
        return html.toString();
    }

    private static boolean isUserAuthorized(HttpServletRequest request) {
        if (request == null) return false;
        if (request.getCookies() == null) return false;
        for (Cookie cookie : request.getCookies()) {
            if ("authorized".equals(cookie.getName()) && "true".equals(cookie.getValue())) {
                return true;
            }
        }
        return false;
    }
}
