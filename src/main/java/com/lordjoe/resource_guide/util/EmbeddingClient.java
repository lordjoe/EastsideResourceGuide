package com.lordjoe.resource_guide.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbeddingClient {

    private static final String EMBEDDING_URL = "http://localhost:8000/embed"; // Update if needed
    private static final ObjectMapper mapper = new ObjectMapper();
    public class EmbeddingResponse {
        public List<List<Double>> embeddings;
    }
    public static List<double[]> getEmbeddings(List<String> texts) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:8000/embed").openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        // Build JSON payload
        Map<String, List<String>> payload = new HashMap<>();
        payload.put("texts", texts);
        mapper.writeValue(conn.getOutputStream(), payload);

        // Deserialize wrapper object
        EmbeddingResponse response = mapper.readValue(conn.getInputStream(), EmbeddingResponse.class);
        List<double[]> result = new ArrayList<>();
        for (List<Double> vector : response.embeddings) {
            double[] arr = new double[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                arr[i] = vector.get(i);
            }
            result.add(arr);
        }

        return result;
    }

    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}

