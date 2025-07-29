package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.*;

import java.util.List;

public class TestResourceCategorizer {

    public static void main(String[] args) throws Exception {
        // Load guide data
        Guide.Instance.guaranteeLoaded();

        // Get uncategorized resources
        Catagory uncat = Guide.Instance.getCatagoryByName("Unclassified");
        if (uncat == null) {
            System.err.println("Unclassified category not found.");
            return;
        }

        List<Resource> toClassify = uncat.getResources();

        // Get candidate categories (excluding "Unclassified")
        List<Catagory> allCats = Guide.Instance.getCatagories();
        List<Catagory> candidates = allCats.stream()
                .filter(c -> !c.getName().equalsIgnoreCase("Unclassified"))
                .toList();

        // Run matching
        for (Resource res : toClassify) {
            List<ResourceCategorizer.CategoryScore> topMatches =
                    ResourceCategorizer.findTopMatches(res, candidates, 3);

            System.out.println("Resource: " + res.getName() + " (ID: " + res.getId() + ")");
            for (ResourceCategorizer.CategoryScore match : topMatches) {
                System.out.printf("  ↳ Match: %s (ID: %d), Score: %.2f%n",
                        match.category.getName(), match.category.getId(), match.score);
            }
            System.out.println("--------------------------------------------------");
        }
    }
}
