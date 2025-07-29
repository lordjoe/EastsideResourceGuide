package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.*;

import java.util.*;

public class ResourceComparer {

    public static void compareResources(List<Resource> flatList, List<Catagory> structuredList) {
        // Flatten all structured resources into a list
        List<Resource> structuredResources = extractAllResources(structuredList);

        for (Resource flat : flatList) {
            for (Resource structured : structuredResources) {
                if (areSimilar(flat, structured)) {
                    System.out.println("=== MATCH FOUND ===");
                    System.out.println("Flat Resource (ID " + flat.getId() + "): " + resourceSummary(flat));
                    System.out.println("Structured Resource (ID " + structured.getId() + "): " + resourceSummary(structured));
                    System.out.println();
                }
            }
        }
    }

    private static List<Resource> extractAllResources(List<Catagory> categories) {
        List<Resource> result = new ArrayList<>();
        for (Catagory cat : categories) {
            result.addAll(cat.getResources());
            for (SubCatagory sub : cat.getSubCatagories()) {
                result.addAll(sub.getResources());
            }
        }
        return result;
    }

    private static boolean areSimilar(Resource a, Resource b) {
        if (a == null || b == null) return false;

        // Normalize and compare names
        String nameA = normalize(a.getName());
        String nameB = normalize(b.getName());
        if (!nameA.equals(nameB)) return false;

        // Optionally compare other fields
        if (!Objects.equals(normalize(a.getPhone()), normalize(b.getPhone()))) return false;
        if (!Objects.equals(normalize(a.getAddress()), normalize(b.getAddress()))) return false;

        return true;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replaceAll("[^a-z0-9]", ""); // alphanumeric normalization
    }

    private static String resourceSummary(Resource r) {
        return String.format("Name: %s | Phone: %s | Address: %s | Website: %s",
                r.getName(), r.getPhone(), r.getAddress(), r.getWebsite());
    }
}
