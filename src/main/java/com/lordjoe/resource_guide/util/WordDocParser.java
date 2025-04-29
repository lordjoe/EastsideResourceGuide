package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceUrlDAO;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceUrl;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordDocParser {

    private static final Pattern PHONE_PATTERN = Pattern.compile("\\(\\d{3}\\)\\s*\\d{3}-\\d{4}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+|www\\.\\S+");
    private static final Pattern HOURS_PATTERN = Pattern.compile("(?i).*\\b(mon|tue|wed|thu|fri|sat|sun)\\b.*");

    private static String currentCategory = null;
    private static String currentSubcategory = null;

    public static void parseAndInsertDocx(File filepath) throws Exception {
        String filename = filepath.getName();
        if (filename.startsWith(".~lock"))
            return; // Skip temp files

        currentCategory = getCategoryName(filename);

        try (InputStream fis = new FileInputStream(filepath);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            CommunityResource currentResource = null;

            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                if (text.isEmpty())
                    continue;

                boolean isHeading2 = "Heading2".equals(para.getStyle());
                boolean isFullyBold = para.getRuns().stream().allMatch(run -> run.isBold());
                boolean isBlackText = para.getRuns().stream().allMatch(run -> {
                    String color = run.getColor();
                    return color == null || color.equalsIgnoreCase("000000");
                });

                if (isHeading2) {
                    currentSubcategory = text;
                    continue;
                }

                if (isFullyBold && isBlackText) {
                    // New Resource
                    if (currentResource != null) {
                        saveCurrentResource(currentResource);
                    }
                    currentResource = new CommunityResource();
                    currentResource.setCategory(currentCategory);
                    currentResource.setSubcategory(currentSubcategory);
                    currentResource.setName(text);
                    continue;
                }

                if (currentResource != null) {
                    if (isPhoneNumber(text)) {
                        currentResource.setPhonePrimary(extractPhone(text));
                    } else if (isUrl(text)) {
                        currentResource.setWebsite(extractUrl(text));
                    } else if (isEmail(text)) {
                        currentResource.setEmail(extractEmail(text));
                    } else if (isHours(text)) {
                        if (currentResource.getNotes() == null) {
                            currentResource.setNotes(text);
                        } else {
                            currentResource.setNotes(currentResource.getNotes() + " " + text);
                        }
                    } else {
                        // Append to description
                        if (currentResource.getDescription() == null) {
                            currentResource.setDescription(text);
                        } else {
                            currentResource.setDescription(currentResource.getDescription() + " " + text);
                        }
                    }
                }
            }

            if (currentResource != null) {
                saveCurrentResource(currentResource);
            }
        }
    }

    private static void saveCurrentResource(CommunityResource resource) throws SQLException {
        int resourceId = CommunityResourceDAO.insert(resource);
        if (resource.getWebsite() != null) {
            ResourceUrl url = new ResourceUrl(resourceId, "main", resource.getWebsite());
            ResourceUrlDAO.insert(url);
        }
    }

    private static boolean isPhoneNumber(String text) {
        return PHONE_PATTERN.matcher(text).find();
    }

    private static boolean isUrl(String text) {
        return URL_PATTERN.matcher(text).find();
    }

    private static boolean isEmail(String text) {
        return EMAIL_PATTERN.matcher(text).find();
    }

    private static boolean isHours(String text) {
        return HOURS_PATTERN.matcher(text).find();
    }

    private static String extractPhone(String text) {
        Matcher matcher = PHONE_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    private static String extractUrl(String text) {
        Matcher matcher = URL_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    private static String extractEmail(String text) {
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    private static String getCategoryName(String filename) {
        filename = StringUtils.FileToCatagory(filename);
        int dot = filename.indexOf('.');
        if (dot > 0)
            filename = filename.substring(0, dot);
        return filename.replace('_', ' ').trim();
    }
}
