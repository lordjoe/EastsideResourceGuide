package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceDescriptionDAO;
import com.lordjoe.resource_guide.dao.ResourceSiteDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import com.lordjoe.resource_guide.model.ResourceSite;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WordDocParser {

    private static final Pattern PHONE_PATTERN = Pattern.compile("\\(\\d{3}\\) \\d{3}-\\d{4}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\\b");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+|www\\.\\S+");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "^\\d+\\s+.+\\b(St|Street|Ave|Avenue|Blvd|Road|Rd|Dr|Drive|Ct|Court|Pl|Place|Ln|Lane)\\b.*",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern HOURS_PATTERN = Pattern.compile(
            "(?i)\\b(?:mon(?:day)?|tue(?:sday)?|wed(?:nesday)?|thu(?:rsday)?|fri(?:day)?|sat(?:urday)?|sun(?:day)?)s?\\b[\\s,:-]*\\d{1,2}(?::\\d{2})?\\s*(?:AM|PM)?\\s*(?:-|to|–)\\s*\\d{1,2}(?::\\d{2})?\\s*(?:AM|PM)?"
    );

    public static void parseAndInsertDocx(File file) throws Exception {
        if (file.getName().startsWith(".~lock")) return;

        try (InputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            String categoryName = file.getName().replace("_", " ").replace(".docx", "").trim();
            Catagory category = CategoryUtils.CreateCatagory(categoryName);
            int currentCategoryId = category.getId();
            int currentSubcategoryId = -1;

            CommunityResource currentResource = null;
            List<String> currentDescriptions = new ArrayList<>();
            List<String> phoneLines = new ArrayList<>();
            List<String> addressLines = new ArrayList<>();
            boolean readingDescription = false;
            boolean readDescription = false;
            boolean readingAddress = false;
            boolean inBlock = false;
            StringBuilder blockBuffer = new StringBuilder();

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                if (text.isEmpty()) continue;

                if (text.equalsIgnoreCase("[BLOCK_START]")) {
                    inBlock = true;
                    blockBuffer.setLength(0);
                    continue;
                }

                if (text.equalsIgnoreCase("[BLOCK_END]")) {
                    inBlock = false;
                    String blockText = blockBuffer.toString();
                    if (currentResource == null) {
                        int ownerId = currentSubcategoryId != -1 ? currentSubcategoryId : currentCategoryId;
                        ResourceDescriptionDAO.insert(new ResourceDescription(ownerId, blockText, true));
                    } else {
                        currentDescriptions.add("[BLOCK]\n" + blockText);
                    }
                    continue;
                }

                if (inBlock) {
                    blockBuffer.append(text).append("\n");
                    continue;
                }

                if (looksLikeSubcategory(para)) {
                    saveResource(currentResource, currentDescriptions, phoneLines, addressLines);
                    currentResource = new CommunityResource(text, ResourceType.Subcategory, currentCategoryId);
                    currentSubcategoryId = CommunityResourceDAO.insert(currentResource);
                    currentResource = null;
                    currentDescriptions.clear();
                    phoneLines.clear();
                    addressLines.clear();
                    readingAddress = false;
                    readingDescription = false;
                    readDescription = false;
                    continue;
                }

                if (looksLikeResource(para)) {
                    saveResource(currentResource, currentDescriptions, phoneLines, addressLines);
                    currentResource = new CommunityResource(text, ResourceType.Resource,
                            currentSubcategoryId != -1 ? currentSubcategoryId : currentCategoryId);
                    currentDescriptions.clear();
                    phoneLines.clear();
                    addressLines.clear();
                    readingAddress = false;
                    readDescription = false;
                    readingDescription = true; // Start assuming description
                    continue;
                }

                // Main parsing logic
                if (currentResource != null) {
                    // If we're collecting description and hit a structured field, stop
                    if (readingDescription) {
                        if (PHONE_PATTERN.matcher(text).find() ||
                                EMAIL_PATTERN.matcher(text).find() ||
                                URL_PATTERN.matcher(text).find() ||
                                HOURS_PATTERN.matcher(text).find() ||
                                ADDRESS_PATTERN.matcher(text).find()) {
                               readDescription = true;
                               readingDescription = false;
                        } else {
                            currentDescriptions.add(text);
                            continue;
                        }
                    }

                    // Handle address continuation
                    if (readingAddress) {
                        if (PHONE_PATTERN.matcher(text).find() ||
                                EMAIL_PATTERN.matcher(text).find() ||
                                URL_PATTERN.matcher(text).find() ||
                                HOURS_PATTERN.matcher(text).find()) {
                            readingAddress = false;
                            // fall through to process as structured field
                        } else {
                            addressLines.add(text);
                            continue;
                        }
                    }
                    if (ADDRESS_PATTERN.matcher(text).find()) {
                        addressLines.add(text);
                        readingAddress = true;
                    } else if (PHONE_PATTERN.matcher(text).find()) {
                        phoneLines.add(text);
                    } else if (EMAIL_PATTERN.matcher(text).find()) {
                        String realEmail = text;
                        if(realEmail.startsWith("Email:")) {
                            realEmail = realEmail.replace("Email:","");
                            realEmail = realEmail.trim();
                        }
                        currentResource.setEmail(realEmail);
                    } else if (URL_PATTERN.matcher(text).find()) {
                        currentResource.setWebsite(text);
                    } else if (HOURS_PATTERN.matcher(text).find()) {
                        currentResource.setHours(text);
                    } else {
                        if(!readDescription)
                            currentDescriptions.add(text); // fallback if unmatched
                        else {
                            if(addressLines.size()  == 0) {
                                addressLines.add(text);
                                readingAddress = true;
                            }
                        }
                    }
                } else {
                    // Category-level description
                    ResourceDescriptionDAO.insert(new ResourceDescription(currentCategoryId, text, false));
                }
            }

            // Final resource save
            saveResource(currentResource, currentDescriptions, phoneLines, addressLines);
        }
    }

    private static void saveResource(CommunityResource resource, List<String> descriptions,
                                     List<String> phoneLines, List<String> addressLines) throws Exception {
        if (resource != null) {
            if (!phoneLines.isEmpty()) {
                resource.setPhone(String.join("\n", phoneLines));
            }
            if (!addressLines.isEmpty()) {
                String join = String.join("\n", addressLines);
                resource.setAddress(join);
            }
            int id = CommunityResourceDAO.insert(resource);
            resource.setId(id);
            for (String desc : descriptions) {
                ResourceDescriptionDAO.insert(new ResourceDescription(id, desc, false));
            }
            if(resource.hasSiteInfo())  {
                ResourceSite rs = new ResourceSite(resource);
                ResourceSiteDAO.insert(rs);
            }
        }
    }

    private static boolean looksLikeSubcategory(XWPFParagraph para) {
        return "Heading2".equalsIgnoreCase(para.getStyle());
    }

    private static boolean looksLikeResource(XWPFParagraph para) {
        return para.getRuns() != null &&
                para.getRuns().stream().anyMatch(run -> run.isBold()) &&
                !looksLikeSubcategory(para);
    }
}
