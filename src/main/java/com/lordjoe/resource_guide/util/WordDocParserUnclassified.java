package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceDescriptionDAO;
import com.lordjoe.resource_guide.dao.ResourceSiteDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import com.lordjoe.resource_guide.model.ResourceSite;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class WordDocParserUnclassified {

    public static void parseDocxToUnclassified(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            List<String> resourceLines = new ArrayList<>();
            boolean inResource = false;

            for (XWPFParagraph para : paragraphs) {
                List<XWPFRun> runs = para.getRuns();
                for (XWPFRun run : runs) {
                    String style = run.getStyle();
                    String text = run.getText(0);
                    System.out.println(text);
                }
                String text = para.getText().trim();
                if (text.isEmpty())
                    continue;

                if (isFullyBold(para)) {
                    if (inResource) {
                        processResourceLines(resourceLines);
                        resourceLines.clear();
                    }
                    inResource = true;
                }

                if (inResource) {
                    resourceLines.add(text);
                }
            }

            // Handle last resource
            if (!resourceLines.isEmpty()) {
                processResourceLines(resourceLines);
            }
        }
    }

    public static void processResourceLines(List<String> lines) throws Exception {
        if (lines.isEmpty()) return;

        CommunityResource resource = new CommunityResource();
        resource.setName(lines.get(0).trim());
        resource.setType(ResourceType.Resource);
        resource.setParentId(CategoryUtils.getUnclassifiedId());

        String currentField = null;
        StringBuilder buffer = new StringBuilder();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            // Identify new field (delimiter) if present
            if (line.contains("Phone |")) {
                flushToResource(resource, currentField, buffer.toString().trim());
                currentField = "Phone";
                buffer = new StringBuilder(line.substring(line.indexOf("Phone |") + 7).trim());
            } else if (line.contains("Website |")) {
                flushToResource(resource, currentField, buffer.toString().trim());
                currentField = "Website";
                buffer = new StringBuilder(line.substring(line.indexOf("Website |") + 9).trim());
            } else if (line.contains("Email |")) {
                flushToResource(resource, currentField, buffer.toString().trim());
                currentField = "Email";
                buffer = new StringBuilder(line.substring(line.indexOf("Email |") + 7).trim());
            } else if (line.contains("Hours |")) {
                flushToResource(resource, currentField, buffer.toString().trim());
                currentField = "Hours";
                buffer = new StringBuilder(line.substring(line.indexOf("Hours |") + 7).trim());
            } else if (line.contains("Address |")) {
                flushToResource(resource, currentField, buffer.toString().trim());
                currentField = "Address";
                buffer = new StringBuilder(line.substring(line.indexOf("Address |") + 9).trim());
            } else if (line.contains("Description |")) {
                flushToResource(resource, currentField, buffer.toString().trim());
                currentField = "Description";
                buffer = new StringBuilder(line.substring(line.indexOf("Description |") + 13).trim());
            } else {
                // Continuation of current field
                buffer.append(" ").append(line.trim());
            }
        }

        // Handle the last section
        flushToResource(resource, currentField, buffer.toString().trim());

        // Save the resource
        WordDocParserUnclassified.storeResource(resource);
    }

    // Helper method to assign to fields
    private static void flushToResource(CommunityResource resource, String field, String value) {
        if (value == null || value.isEmpty()) return;
        if (field == null || field.isEmpty()) return;

        switch (field) {
            case "Phone" -> resource.setPhone(value);
            case "Website" -> resource.setWebsite(value);
            case "Email" -> resource.setEmail(value);
            case "Hours" -> resource.setHours(value);
            case "Address" -> resource.setAddress(value);
            case "Description"    -> {
                if (resource.getDescription() == null) {
                    resource.setDescription(value);
                } else {
                    resource.setDescription(resource.getDescription() + " " + value);
                }
            }
        }
    }




    private static void storeResource(CommunityResource resource ) throws Exception {
        int id = CommunityResourceDAO.insert(resource);
        resource.setId(id);
        String description = resource.getDescription();
        if (description != null && !description.isEmpty()) {
            ResourceDescription desc = new ResourceDescription(id, description, false);
            ResourceDescriptionDAO.insert(desc);

        }
        if (hasSiteInfo(resource)) {
            ResourceSite rs = new ResourceSite(resource);
            ResourceSiteDAO.insert(rs);
        }
    }
    public static boolean hasSiteInfo(CommunityResource res) {
        return res.getAddress() != null || res.getPhone() != null ||
                res.getEmail() != null || res.getWebsite() != null;
    }
 

    private static String extractValue(String currentLine, List<String> lines, int index) {
        StringBuilder value = new StringBuilder(currentLine.substring(currentLine.indexOf('|') + 1).trim());
        for (int i = index + 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.contains("|")) break;
            value.append(" ").append(line);
        }
        return value.toString().trim();
    }

    private static boolean isFullyBold(XWPFParagraph para) {
        for (XWPFRun run : para.getRuns()) {
            if (!run.isBold()) return false;
        }
        return !para.getRuns().isEmpty();
    }
}
