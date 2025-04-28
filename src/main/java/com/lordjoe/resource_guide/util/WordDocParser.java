package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.dao.CategoryDescriptionDAO;
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
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(".*\\d+\\s+.*(St|Street|Ave|Avenue|Blvd|Boulevard|Rd|Road|Dr|Drive|Ct|Court|Pl|Place|Ln|Lane).*");

    private static String currentCategory = null;
    private static String currentSubcategory = null;
    private static boolean foundFirstOrg = false;
    private static StringBuilder pendingCategoryDescription = new StringBuilder();
    private static StringBuilder pendingSubcategoryDescription = new StringBuilder();

    public static void parseAndInsertDocx(File filepath) throws Exception {
        String name = filepath.getName();
        if (name.startsWith(".~lock"))
            return;

        currentCategory = getCategoryName(name);
        currentSubcategory = null;
        foundFirstOrg = false;
        pendingCategoryDescription.setLength(0);
        pendingSubcategoryDescription.setLength(0);

        try (InputStream fis = new FileInputStream(filepath);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            CommunityResource currentResource = null;

            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                if (text.isEmpty())
                    continue;

                if (isFullyBold(para)) {
                    if (!foundFirstOrg) {
                        if (pendingCategoryDescription.length() > 0) {
                            saveCategoryDescription(currentCategory, null, pendingCategoryDescription.toString());
                        }
                    } else if (pendingSubcategoryDescription.length() > 0 && currentSubcategory != null) {
                        saveCategoryDescription(currentCategory, currentSubcategory, pendingSubcategoryDescription.toString());
                    }

                    currentSubcategory = text;
                    foundFirstOrg = true;
                    pendingSubcategoryDescription.setLength(0);
                    continue;
                }

                if (!foundFirstOrg) {
                    pendingCategoryDescription.append(text).append(" ");
                    continue;
                }

                if (currentSubcategory != null && !foundFirstOrg) {
                    pendingSubcategoryDescription.append(text).append(" ");
                    continue;
                }

                if (looksLikeOrganizationName(text)) {
                    if (currentResource != null) {
                        saveCurrentResource(currentResource);
                    }
                    currentResource = new CommunityResource();
                    currentResource.setCategory(currentCategory);
                    currentResource.setSubcategory(currentSubcategory);
                    currentResource.setName(text);
                } else if (currentResource != null) {
                    Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
                    Matcher urlMatcher = URL_PATTERN.matcher(text);
                    Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
                    Matcher addressMatcher = ADDRESS_PATTERN.matcher(text);

                    if (phoneMatcher.find() && currentResource.getPhonePrimary() == null) {
                        currentResource.setPhonePrimary(phoneMatcher.group());
                    }
                    if (urlMatcher.find() && currentResource.getWebsite() == null) {
                        currentResource.setWebsite(urlMatcher.group());
                    }
                    if (emailMatcher.find() && currentResource.getEmail() == null) {
                        currentResource.setEmail(emailMatcher.group());
                    }
                    if (addressMatcher.find() && currentResource.getAddressLine1() == null) {
                        currentResource.setAddressLine1(addressMatcher.group());
                    }
                    if (currentResource.getDescription() == null) {
                        currentResource.setDescription(text);
                    } else {
                        currentResource.setDescription(currentResource.getDescription() + " " + text);
                    }
                }
            }

            if (currentResource != null) {
                saveCurrentResource(currentResource);
            }
        }
    }

    private static boolean isFullyBold(XWPFParagraph para) {
        return para.getRuns().stream().allMatch(r -> r.isBold());
    }

    private static void saveCurrentResource(CommunityResource resource) throws SQLException {
        int resourceId = CommunityResourceDAO.insert(resource);
        if (resource.getWebsite() != null) {
            ResourceUrl url = new ResourceUrl(resourceId, "main", resource.getWebsite());
            ResourceUrlDAO.insert(url);
        }
    }

    private static void saveCategoryDescription(String category, String subcategory, String text) throws SQLException {
        if (text == null || text.trim().isEmpty()) return;
        CategoryDescriptionDAO.insert(category, subcategory, text.trim());
    }

    private static boolean looksLikeOrganizationName(String text) {
        return !text.contains("@") && !text.matches(".*\\d{5}.*");
    }

    private static String getCategoryName(String filename) {
        filename = StringUtils.FileToCatagory(filename);
        int dot = filename.indexOf('.');
        if (dot > 0)
            filename = filename.substring(0, dot);
        return filename.replace('_', ' ').trim();
    }
}
