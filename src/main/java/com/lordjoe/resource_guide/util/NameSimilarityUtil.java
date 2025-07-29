package com.lordjoe.resource_guide.util;


import java.util.*;

public class NameSimilarityUtil {

    // Compute similarity score between 0.0 and 1.0
    public static double computeNameSimilarity(String name1, String name2) {
        Set<String> tokens1 = tokenizeAndNormalize(name1);
        Set<String> tokens2 = tokenizeAndNormalize(name2);

        if (tokens1.isEmpty() || tokens2.isEmpty())
            return 0.0;

        int intersection = 0;
        for (String token : tokens1) {
            if (tokens2.contains(token))
                intersection++;
        }

        double jaccard = (double) intersection / (tokens1.size() + tokens2.size() - intersection);

        return jaccard;
    }

    // Normalize names: lowercase, strip punctuation, split on spaces
    private static Set<String> tokenizeAndNormalize(String name) {
        String cleaned = name.toLowerCase().replaceAll("[^a-z\\s]", "").trim();
        String[] parts = cleaned.split("\\s+");
        return new HashSet<>(Arrays.asList(parts));
    }

    // Find best match from a list
    public static MatchResult findBestMatch(String candidate, List<String> names) {
        double bestScore = 0.0;
        String bestMatch = null;

        for (String name : names) {
            double score = computeNameSimilarity(candidate, name);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = name;
            }
        }

        return new MatchResult(bestMatch, bestScore);
    }

    public record MatchResult(String name, double score) {
        public boolean isConfidentMatch(double threshold) {
            return score >= threshold;
        }
    }
}
