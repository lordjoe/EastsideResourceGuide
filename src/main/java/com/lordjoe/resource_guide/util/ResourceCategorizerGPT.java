package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.net.http.*;
import java.util.*;

public class ResourceCategorizerGPT {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static class CategoryScore {
        public final Catagory cat;
        public final int score;

        public CategoryScore(Catagory cat, int score) {
            this.cat = cat;
            this.score = score;
        }
    }

    public static List<CategoryScore> findTopMatches(Resource resource, List<Catagory> categories) throws Exception {
        List<CategoryScore> scored = new ArrayList<>();

        for (Catagory cat : categories) {
            String prompt = buildPrompt(resource, cat);
            int score = getSimilarityScore(prompt);
            scored.add(new CategoryScore(cat, score));
        }

        scored.sort((a, b) -> Integer.compare(b.score, a.score)); // descending
        return scored.subList(0, Math.min(3, scored.size()));
    }

    private static String buildPrompt(Resource res, Catagory cat) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a helpful assistant. Given the following:\n\n");
        sb.append("RESOURCE:\n");
        sb.append("Name: ").append(res.getName()).append("\n");
        if (res.getDescription() != null) sb.append("Description: ").append(res.getDescription()).append("\n");
        if (res.getWebsite() != null) sb.append("Website: ").append(res.getWebsite()).append("\n");
        if (res.getEmail() != null) sb.append("Email: ").append(res.getEmail()).append("\n");
        if (res.getAddress() != null) sb.append("Address: ").append(res.getAddress()).append("\n");

        sb.append("\nCATEGORY:\n");
        sb.append("Name: ").append(cat.getName()).append("\n");
        if (cat.getDescription() != null) sb.append("Description: ").append(cat.getDescription()).append("\n");

        sb.append("\nOn a scale from 1 (not similar) to 100 (very similar), how similar is the resource to this category?\n");
        sb.append("Return only the number as an integer, no explanation.");

        return sb.toString();
    }

    private static int getSimilarityScore(String prompt) throws Exception {
        ObjectNode reqBody = mapper.createObjectNode();
        reqBody.put("model", "gpt-4o");
        reqBody.putArray("messages")
                .addObject()
                .put("role", "user")
                .put("content", prompt);
        reqBody.put("temperature", 0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(OPENAI_URL))
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString()))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = mapper.readTree(response.body());
        JsonNode choices = root.get("choices");
        JsonNode jsonNode = choices.get(0);
        JsonNode jsonNode1 = jsonNode.get("message");
        JsonNode jsonNode2 = jsonNode1.get("content");
        String text = jsonNode2.asText();
        String result = text.trim();

        try {
            return Integer.parseInt(result.replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            System.err.println("Unexpected result from GPT: " + result);
            return 0;
        }
    }
}

