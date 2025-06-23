package com.lordjoe.sandhurst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressUtils {

    public static String normalizeAddress(String address) {
        if (address == null) return "";
        // Remove trailing "Unit X", "#X", "Apt X", etc.
        return address.replaceAll("(?i)(\\s+(unit|#|apt|apartment)\\s*\\w+)$", "")
                .trim()
                .toLowerCase();
    }

    public static Map<String, Integer> buildNormalizedAddressMap(List<House> houses) {
        Map<String, Integer> map = new HashMap<>();
        for (House house : houses) {
            String normalized = normalizeAddress(house.getAddress());
            map.put(normalized, house.getId());
        }
        return map;
    }

    public static Integer findClosestHouseId(String rawAddress, Map<String, Integer> normalizedMap) {
        String normalizedInput = normalizeAddress(rawAddress);

        // Direct match first
        if (normalizedMap.containsKey(normalizedInput))
            return normalizedMap.get(normalizedInput);

        // Try startsWith match
        for (String key : normalizedMap.keySet()) {
            if (normalizedInput.startsWith(key))
                return normalizedMap.get(key);
        }

        // Final fallback: simple edit distance
        int bestScore = Integer.MAX_VALUE;
        Integer bestId = null;
        for (Map.Entry<String, Integer> entry : normalizedMap.entrySet()) {
            int dist = levenshteinDistance(normalizedInput, entry.getKey());
            if (dist < bestScore) {
                bestScore = dist;
                bestId = entry.getValue();
            }
        }

        return bestId;
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,     // deletion
                                dp[i][j - 1] + 1),    // insertion
                        dp[i - 1][j - 1] + cost); // substitution
            }
        }
        return dp[a.length()][b.length()];
    }
}

