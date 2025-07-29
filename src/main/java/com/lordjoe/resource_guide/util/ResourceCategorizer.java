package com.lordjoe.resource_guide.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordjoe.resource_guide.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ResourceCategorizer {

    public static class CategoryScore {
        public final Catagory category;
        public final double score;

        public CategoryScore(Catagory category, double score) {
            this.category = category;
            this.score = score;
        }
    }

    public static CategoryScore findBestCategory(Resource resource, List<Catagory> categories) throws Exception {
        String resourceText = resource.getName() + "\n" + (resource.getDescription() != null ? resource.getDescription() : "");
        List<String> texts = new ArrayList<>();
        texts.add(resourceText);
        for (Catagory c : categories) {
            texts.add(c.getName() + "\n" + (c.getDescription() != null ? c.getDescription() : ""));
        }

        List<double[]> vectors = EmbeddingClient.getEmbeddings(texts);
        double[] resourceVec = vectors.get(0);

        List<CategoryScore> results = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            double[] categoryVec = vectors.get(i + 1);
            double similarity = EmbeddingClient.cosineSimilarity(resourceVec, categoryVec);
            results.add(new CategoryScore(categories.get(i), similarity));
        }

        results.sort((a, b) -> Double.compare(b.score, a.score));
        return results.get(0); // Top match
    }

    public static List<CategoryScore> findTopMatches(Resource resource, List<Catagory> categories, int topN) throws Exception {
        String resourceText = resource.getName() + "\n" + (resource.getDescription() != null ? resource.getDescription() : "");
        List<String> texts = new ArrayList<>();
        texts.add(resourceText);
        for (Catagory c : categories) {
            texts.add(c.getName() + "\n" + (c.getDescription() != null ? c.getDescription() : ""));
        }

        List<double[]> vectors = EmbeddingClient.getEmbeddings(texts);
        double[] resourceVec = vectors.get(0);

        List<CategoryScore> results = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            double[] categoryVec = vectors.get(i + 1);
            double similarity = EmbeddingClient.cosineSimilarity(resourceVec, categoryVec);
            results.add(new CategoryScore(categories.get(i), similarity));
        }

        results.sort((a, b) -> Double.compare(b.score, a.score));
        return results.subList(0, Math.min(topN, results.size()));
    }

    public static class EmbeddingClient {
        public static class EmbeddingResponse {
            public List<List<Double>> embeddings;

            public EmbeddingResponse() {}
        }

        public static List<double[]> getEmbeddings(List<String> texts) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            URL url = new URL("http://localhost:8000/embed");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("texts", texts);
            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, jsonMap);
            }

            EmbeddingResponse response = mapper.readValue(conn.getInputStream(), EmbeddingResponse.class);

            List<double[]> result = new ArrayList<>();
            for (List<Double> vec : response.embeddings) {
                double[] arr = new double[vec.size()];
                for (int i = 0; i < vec.size(); i++) {
                    arr[i] = vec.get(i);
                }
                result.add(arr);
            }
            return result;
        }

        public static double cosineSimilarity(double[] a, double[] b) {
            double dot = 0.0, normA = 0.0, normB = 0.0;
            for (int i = 0; i < a.length; i++) {
                dot += a[i] * b[i];
                normA += a[i] * a[i];
                normB += b[i] * b[i];
            }
            return dot / (Math.sqrt(normA) * Math.sqrt(normB));
        }
    }
}
