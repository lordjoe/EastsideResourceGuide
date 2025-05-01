package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.Catagory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

public class WordDocParser {

    private static final Pattern PHONE_PATTERN = Pattern.compile("\\(\\d{3}\\) \\d{3}-\\d{4}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\\b");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+|www\\.\\S+");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(".*\\d+\\s+.*(St|Street|Ave|Avenue|Blvd|Road|Rd|Dr|Drive|Ct|Court|Pl|Place|Ln|Lane).*", Pattern.CASE_INSENSITIVE);

    private static boolean inBlock = false;
    private static StringBuilder blockBuffer = new StringBuilder();

    private static int currentCategoryId = -1;
    private static int currentSubcategoryId = -1;
    private static int currentResourceId = -1;

    public static void parseAndInsertDocx(File file) throws Exception {
        if (file.getName().startsWith(".~lock")) return;

        try (InputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            String categoryName = file.getName().replace("_", " ").replace(".docx", "").trim();
            Catagory catagory = CategoryUtils.CreateCatagory(categoryName);
              currentCategoryId = catagory.getId();

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                if (text.isEmpty()) continue;

                if (text.equalsIgnoreCase("[BLOCK_START]")) {
                    inBlock = true;
                    blockBuffer.setLength(0);
                    continue;
                }

//                if (text.equalsIgnoreCase("[BLOCK_END]")) {
//                    inBlock = false;
//                    saveBlock();
//                    continue;
//                }

                if (inBlock) {
                    blockBuffer.append(text).append("\n");
                    continue;
                }

//                if (looksLikeSubcategory(para)) {
//                    CommunityResource subcategory = new CommunityResource("SubCategory", currentCategoryId, text);
//                    currentSubcategoryId = CommunityResourceDAO.insert(subcategory);
//                    currentResourceId = -1;
//                    continue;
//                }
//
//                if (looksLikeResource(para)) {
//                    CommunityResource resource = new CommunityResource("Resource",
//                            currentSubcategoryId != -1 ? currentSubcategoryId : currentCategoryId,
//                            text);
//                    currentResourceId = CommunityResourceDAO.insert(resource);
//                    continue;
//                }
//
//                if (currentResourceId != -1) {
//                    processResourceDetail(currentResourceId, text);
//                } else {
//                    // text before any subcat/resource => Category description
//                    ResourceDescription desc = new ResourceDescription(currentCategoryId, text, false);
//                    ResourceDescriptionDAO.insert(desc);
//                }
//            }


        }
    }

//    private static void processResourceDetail(int resourceId, String text) throws Exception {
//        if (PHONE_PATTERN.matcher(text).find()) {
//            CommunityResourceDAO.updatePhone(resourceId, text);
//        } else if (EMAIL_PATTERN.matcher(text).find()) {
//            CommunityResourceDAO.updateEmail(resourceId, text);
//        } else if (URL_PATTERN.matcher(text).find()) {
//            CommunityResourceDAO.updateWebsite(resourceId, text);
//        } else if (ADDRESS_PATTERN.matcher(text).find()) {
//            CommunityResourceDAO.updateAddress(resourceId, text);
//        } else {
//            ResourceDescriptionDAO.insert(new ResourceDescription(resourceId, text, false));
//        }
//    }

//    private static boolean looksLikeSubcategory(XWPFParagraph para) {
//        return para.getStyle() != null && para.getStyle().equals("Heading2");
//    }
//
//    private static boolean looksLikeResource(XWPFParagraph para) {
//        return para.getRuns().stream().anyMatch(run -> run.isBold()) && !looksLikeSubcategory(para);
//    }
//
//    private static void saveBlock() throws Exception {
//        if (blockBuffer.length() > 0) {
//            ResourceDescription block = new ResourceDescription(
//                    (currentResourceId != -1 ? currentResourceId : (currentSubcategoryId != -1 ? currentSubcategoryId : currentCategoryId)),
//                    blockBuffer.toString(),
//                    true);
//            ResourceDescriptionDAO.insert(block);
//        }
     }
 

}
