package com.lordjoe.resource_guide.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLValidator {

    public static boolean isValidURL(String urlString) {
        try {
            if (urlString.equalsIgnoreCase("https://app.leg.wa.gov/districtfinder"))
                System.out.println(urlString);

            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();

            // Act like a real browser
            huc.setRequestMethod("GET");
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            huc.setConnectTimeout(5000);
            huc.setReadTimeout(5000);
            huc.connect();

            int code = huc.getResponseCode();
            // Accept 2xx (OK) and 3xx (redirects)
            if (code >= 200 && code < 400)
                return true;
            if (code == 404) {
                System.out.println(urlString + " 404 " + code);
                return Response404Checker(huc);
            }
            if (code == 403)
                return true;

            System.out.println(urlString + " " + code);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Response404Checker(HttpURLConnection huc) throws IOException {
        // Read the content body
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(huc.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }

        String html = content.toString();

        // If the page has significant HTML content (say > 1000 characters), assume it's valid
        if (html.length() > 1000 && html.contains("<html")) {
            return true;
        }
        return false;
    }


}
