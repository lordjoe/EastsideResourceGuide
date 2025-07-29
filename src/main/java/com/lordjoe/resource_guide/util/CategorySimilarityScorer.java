package com.lordjoe.resource_guide.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CategorySimilarityScorer {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY"); // set this in env
    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public static List<String> getTopCategories(String resourceName, String resourceDescription, List<String> categoryNames) throws IOException {
        String prompt = buildPrompt(resourceName, resourceDescription, categoryNames);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-4");
        JsonArray messages = new JsonArray();

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", "You are a helpful assistant that ranks the most relevant categories for a resource.");

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", prompt);

        messages.add(systemMsg);
        messages.add(userMsg);
        requestBody.add("messages", messages);

        HttpURLConnection conn = (HttpURLConnection) new URL(OPENAI_ENDPOINT).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        String resultText = jsonResponse.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

        return extractTopCategories(resultText);
    }

    private static String buildPrompt(String name, String description, List<String> categories) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Resource:\nName: ").append(name).append("\nDescription: ").append(description).append("\n\n");
        prompt.append("Categories:\n");
        for (String cat : categories) {
            prompt.append("- ").append(cat).append("\n");
        }
        prompt.append("\nWhich 3 categories best match the resource? List them from most to least similar.");
        return prompt.toString();
    }

    private static List<String> extractTopCategories(String resultText) {
        List<String> topCategories = new ArrayList<>();
        String[] lines = resultText.split("\n");
        for (String line : lines) {
            line = line.replaceAll("^\\d+\\.|^-", "").trim();
            if (!line.isEmpty()) {
                topCategories.add(line);
                if (topCategories.size() >= 3) break;
            }
        }
        return topCategories;
    }

    // Example usage
    public static void main(String[] args) throws  Exception {
        Guide guide = Guide.Instance;
        String key = OPENAI_API_KEY;
        guide.guaranteeLoaded();

        Catagory unclassified = guide.getCatagoryByName("Unclassified");
        List<Resource> resources = unclassified.getResources();

        List<Catagory> allCats = new ArrayList<>(guide.getCatagories());
        allCats.remove(unclassified); // exclude "Unclassified"

        for (Resource res : resources) {
            List<ResourceCategorizerGPT.CategoryScore> matches = ResourceCategorizerGPT.findTopMatches(res, allCats);

            System.out.println("Resource: " + res.getName() + " (ID: " + res.getId() + ")");
            for (ResourceCategorizerGPT.CategoryScore match : matches) {
                System.out.println("  ↳ Match: " + match.cat.getName() + " (ID: " + match.cat.getId() + "), Score: " + match.score);
            }
            System.out.println("--------------------------------------------------");
        }

    }


}
