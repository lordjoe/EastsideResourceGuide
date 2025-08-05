package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.CheckURLValidation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class URLValidator {
    public static Set<String> goodURLs = new HashSet<>();
    public static Set<String> badURLs = new HashSet<>();

    public static boolean needsCheck(String urlString) {
        if (urlString == null)
            return false;
        urlString = urlString.replace("http://", "https://");
        if (!urlString.startsWith("https://"))
            urlString = "https://" + urlString;
        if (goodURLs.contains(urlString))
            return false;

        if (badURLs.contains(urlString))
            return false;

        if (CheckURLValidation.KnownGoodURLS.contains(urlString)) {
            goodURLs.add(urlString);
            return false;
        }
        if (CheckURLValidation.KnownBadURLS.contains(urlString)) {
            badURLs.add(urlString);
            return false;
        }
        return true;
    }


    public static boolean isValidURLWithJsoup(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .followRedirects(true)
                    .get();

            // Just checking if it parsed successfully
            String title = doc.title();
        //    System.out.println("✔ Page loaded: " + title);
            return true;
        } catch (Exception e) {
   //         System.err.println("✘ Failed to load URL: " + url);
  //          e.printStackTrace();
            return false;
        }
    }


    public static boolean isValidURL(String urlString) {
        return isValidURL(urlString, false,false);
    }

    public static boolean isValidURL(String urlString, boolean forceCheck) {
        return isValidURL(urlString, forceCheck,false);
    }

    public static boolean isValidURL(String urlOriginalString, boolean forceCheck,boolean forceHpps) {
        if (urlOriginalString == null)
            return true;
        if(urlOriginalString.contains(" "))
            return false;
        String urlString = urlOriginalString;
        if(forceHpps) {
             urlString = urlString.replace("http://", "https://");
            if (!urlString.startsWith("https://"))
                urlString = "https://" + urlString;
        }

        if (!forceCheck && !needsCheck(urlString)) {
             urlString = urlString.replace("http://", "https://");
            if (!urlString.startsWith("https://"))
                urlString = "https://" + urlString;
             return goodURLs.contains(urlString);
        }
        try {
            boolean ret = false;


            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();

            // Act like a real browser
            huc.setRequestMethod("GET");
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            huc.setConnectTimeout(10000);
            huc.setReadTimeout(10000);
            huc.connect();

            int code = huc.getResponseCode();
            // Accept 2xx (OK) and 3xx (redirects)
            if (code >= 200 && code < 400) {
                System.out.println(" good code" + urlString);
               ret = true;
            }
            if (code == 404) {
                System.out.println(urlString + " 404 " + code);
                ret = Response404Checker(huc);
            }
            if (code == 403)
                ret = true;

            //   System.out.println(urlString + " " + code);
            if (ret) {
                goodURLs.add(urlString);
                CheckURLValidation.KnownGoodURLS.add(urlString);
                System.out.println( "\"" + urlString + "\",");
                return true;
            } else {
                if(isValidURLWithJsoup(urlString)) {
                    goodURLs.add(urlString);
                    CheckURLValidation.KnownGoodURLS.add(urlString);
                    System.out.println( "\"" + urlString + "\",");
                    return true;
                }

                badURLs.add(urlString);
                CheckURLValidation.KnownBadURLS.add(urlString);
                System.out.println("bad " + "\"" + urlString + "\",");

            }
            return false;
        } catch (Exception e) {
            if(e instanceof SSLHandshakeException) {
                urlString = urlString.replace("https://", "http://");
                return isValidURL(urlString,forceCheck,false);
            }
            boolean ret =  isValidURLWithJsoup(urlString);
            if(ret) {
                goodURLs.add(urlString);
                CheckURLValidation.KnownGoodURLS.add(urlString);
                System.out.println( "\"" + urlString + "\",");
                return true;

            }
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
