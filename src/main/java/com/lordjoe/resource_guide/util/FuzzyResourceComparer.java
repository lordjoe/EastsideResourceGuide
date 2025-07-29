package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.*;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;

public class FuzzyResourceComparer {

    private static final int MAX_NAME_DISTANCE = 5; // tune as needed

    public static void compareUncategorizedVsStructured() {
        Guide guide = Guide.Instance;
        guide.guaranteeLoaded();

        Catagory uncat = guide.getUnClassified();
        if (uncat == null) {
            System.err.println("Category 'unCatagorized' not found.");
            return;
        }
        List<Integer>  commonIDs = new ArrayList<>();
        List<Resource> flatList = uncat.getResources();
        List<Resource> structuredList = new ArrayList<>();

        for (Catagory cat : guide.getCatagories()) {
            if (cat == uncat) continue; // skip unCatagorized
            structuredList.addAll(cat.getResources());
            for (SubCatagory sub : cat.getSubCatagories()) {
                structuredList.addAll(sub.getResources());
            }
        }

        compareResources(flatList, structuredList,commonIDs);
        for (Integer commonID : commonIDs) {
            System.out.print(commonID + ",");
        }
        System.out.println();
    }

    public static void compareResources(List<Resource> flatList, List<Resource> structuredList,List<Integer>  commonIDs) {
        LevenshteinDistance distance = LevenshteinDistance.getDefaultInstance();

        for (Resource flat : flatList) {
            for (Resource structured : structuredList) {
                String nameA = normalize(flat.getName());
                String nameB = normalize(structured.getName());

                int dist = distance.apply(nameA, nameB);
                if (dist > MAX_NAME_DISTANCE) continue; // too different

                if (!fieldsRoughlyMatch(flat, structured))
                    continue;
                System.out.println("=== POSSIBLE MATCH ===");
                System.out.println("Flat (ID " + flat.getId() + "): " + summary(flat));
                System.out.println("Structured (ID " + structured.getId() + "): " + summary(structured));
                System.out.println();
                commonIDs.add(flat.getId());
                break;
            }
        }
    }

    private static boolean fieldsRoughlyMatch(Resource a, Resource b) {
        // loosen rules if needed, here we just confirm at least 1 field matches
        return (similarOrNull(a.getPhone(), b.getPhone()) ||
                similarOrNull(a.getAddress(), b.getAddress()) ||
                similarOrNull(a.getWebsite(), b.getWebsite()));
    }

    private static boolean similarOrNull(String a, String b) {
        if (a == null || b == null) return true;
        return normalize(a).contains(normalize(b)) || normalize(b).contains(normalize(a));
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private static String summary(Resource r) {
        return String.format("Name: %s | Phone: %s | Address: %s | Website: %s",
                r.getName(), r.getPhone(), r.getAddress(), r.getWebsite());
    }

    public static void main(String[] args) {
        compareUncategorizedVsStructured();
    }
}
