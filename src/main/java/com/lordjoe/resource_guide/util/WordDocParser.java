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
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+|www\\.\\S+");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(".*\\d+\\s+.*(St|Street|Ave|Avenue|Blvd|Boulevard|Rd|Road|Dr|Drive|Ct|Court|Pl|Place|Ln|Lane).*", Pattern.CASE_INSENSITIVE);

    private static String currentCategory = null;
    private static String currentSubcategory = null;
    private static boolean insideBlock = false;
    private static boolean insideList = false;

    public static void parseAndInsertDocx(File filepath) throws Exception {
        String name = filepath.getName();
        if (name.startsWith(".~lock"))
            return;

        currentCategory = getCategoryName(name);
        currentSubcategory = null;

        try (InputStream fis = new FileInputStream(filepath);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            CommunityResource currentResource = null;

            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                if (text.isEmpty())
                    continue;

                // Block handling
                if (text.equalsIgnoreCase("[BLOCK_START]")) {
                    insideBlock = true;
                    continue;
                }
                if (text.equalsIgnoreCase("[BLOCK_END]")) {
                    insideBlock = false;
                    continue;
                }
                if (insideBlock) {
                    // Skip parsing inside a block
                    continue;
                }

                // List handling
                if (text.equalsIgnoreCase("[LIST_START]")) {
                    insideList = true;
                    continue;
                }
                if (text.equalsIgnoreCase("[LIST_END]")) {
                    insideList = false;
                    continue;
                }

                // Skip phone, email, url single lines (they are handled)
                if (isPhoneNumber(text) || isEmail(text) || isUrl(text))
                    continue;

                // Hours
                if (HoursParser.isHoursLine(text)) {
                    if (currentResource != null && currentResource.getHours() == null) {
                        currentResource.setHours(HoursParser.extractHours(text));
                    }
                    continue;
                }

                // Subcategory: H2-style (could be improved)
                if (looksLikeSubcategory(para)) {
                    currentSubcategory = text;
                    continue;
                }

                // Organization (bold line)
                if (looksLikeOrganizationName(para)) {
                    if (currentResource != null) {
                        saveCurrentResource(currentResource);
                    }
                    currentResource = new CommunityResource();
                    currentResource.setCategory(currentCategory);
                    currentResource.setSubcategory(currentSubcategory);
                    currentResource.setName(text);
                } else if (currentResource != null) {
                    parseDetailsIntoResource(text, currentResource);
                }
            }

            // Save last resource
            if (currentResource != null) {
                saveCurrentResource(currentResource);
            }
        }
    }

    private static void parseDetailsIntoResource(String text, CommunityResource resource) {
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        Matcher urlMatcher = URL_PATTERN.matcher(text);
        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        Matcher addressMatcher = ADDRESS_PATTERN.matcher(text);

        if (phoneMatcher.find() && resource.getPhonePrimary() == null) {
            resource.setPhonePrimary(phoneMatcher.group());
            return;
        }
        if (urlMatcher.find() && resource.getWebsite() == null) {
            resource.setWebsite(urlMatcher.group());
            return;
        }
        if (emailMatcher.find() && resource.getEmail() == null) {
            resource.setEmail(emailMatcher.group());
            return;
        }
        if (addressMatcher.find() && resource.getAddressLine1() == null) {
            resource.setAddressLine1(addressMatcher.group());
            return;
        }

        // Otherwise treat as description
        if (resource.getDescription() == null) {
            resource.setDescription(text);
        } else {
            resource.setDescription(resource.getDescription() + " " + text);
        }
    }

    private static void saveCurrentResource(CommunityResource resource) throws SQLException {
        int resourceId = CommunityResourceDAO.insert(resource);
        if (resource.getWebsite() != null) {
            ResourceUrl url = new ResourceUrl(resourceId, "main", resource.getWebsite());
            ResourceUrlDAO.insert(url);
        }
        System.out.println("Saved: " + resource.getName());
    }

    private static boolean looksLikeOrganizationName(XWPFParagraph para) {
        return para.getRuns().stream().allMatch(run -> run.isBold());
    }

    private static boolean looksLikeSubcategory(XWPFParagraph para) {
        return para.getStyle() != null && para.getStyle().toLowerCase().contains("heading2");
    }

    private static boolean isPhoneNumber(String text) {
        return PHONE_PATTERN.matcher(text).find();
    }

    private static boolean isUrl(String text) {
        return text.startsWith("http") || text.startsWith("www.");
    }

    private static boolean isEmail(String text) {
        return EMAIL_PATTERN.matcher(text).find();
    }

    private static String getCategoryName(String filename) {
        filename = StringUtils.FileToCatagory(filename);
        int dot = filename.indexOf('.');
        if (dot > 0)
            filename = filename.substring(0, dot);
        return filename.replace('_', ' ').trim();
    }
}
