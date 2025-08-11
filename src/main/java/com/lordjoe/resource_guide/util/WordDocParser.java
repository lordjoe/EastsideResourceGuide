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
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class WordDocParser {

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?i).*\\bphone\\b[:\\s]*.*|.*\\(?\\d{3}\\)?[\\s.-]*\\d{3}[\\s.-]*\\d{4}.*"
    );
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\\b");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+|www\\.\\S+");

    private static Set<Integer> savedItems = null;

    public static void parseAndInsertDocx(File file) throws Exception {
        if (file.getName().startsWith(".~lock")) return;
        if (!file.getName().endsWith(".docx")) return;
        savedItems = new HashSet<>();

        Stack<CommunityResource> resourceStack = new Stack<>();
        Stack<List<String>> descriptions = new Stack<>();

        try (InputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            String categoryName = file.getName().replace("_", " ").replace(".docx", "").trim();
            Catagory category = CategoryUtils.CreateCatagory(categoryName);
            int id = category.getId();
              CommunityResource rs = CommunityResourceDAO.create(id, categoryName, ResourceType.Category, null);
            resourceStack.push(rs);
            descriptions.push(new ArrayList<>());
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            ParagraphIterator items = new ParagraphIterator(paragraphs);

            handleItems(items, resourceStack,descriptions,false);
        }
    }

    private static CommunityResource  insertResource( String name,ResourceType type, int parendId) throws SQLException {
         CommunityResource rs = CommunityResourceDAO.create(0, name, type, parendId,null);
       return rs;
    }

    private static boolean isStruckThrough(XWPFParagraph para) {
        boolean hasText = false;
        for (XWPFRun run : para.getRuns()) {
            String text = run.text();
            if (text != null && !text.trim().isEmpty()) {
                hasText = true;
                if (!run.isStrikeThrough()) return false;
            }
        }
        return hasText; // Only skip if paragraph has text and all runs are struck
    }

    private static void handleItems(ParagraphIterator items,
                                    Stack<CommunityResource> resourceStack, Stack<List<String>> descriptions,  boolean inList) throws Exception {
        CommunityResource activeResource = resourceStack.lastElement();
        List<String> currentDescriptions = descriptions.lastElement();
        List<String> phoneLines = new ArrayList<>();
        List<String> addressLines = new ArrayList<>();
        boolean readingDescription = true;
        boolean readDescription = false;
        boolean readingAddress = false;
        boolean inBlock = false;
        //     printResourceStack(resourceStack);

        try {
            StringBuilder blockBuffer = new StringBuilder();
            while (items.hasNext()) {
                activeResource = resourceStack.lastElement();
                currentDescriptions = descriptions.lastElement();
                XWPFParagraph para = items.next();

                if (isStruckThrough(para)) {
                    continue; // Skip struck-through paragraphs
                }
                String text = para.getText().trim();


                if (text.isEmpty()) {
                    //  items.next();
                    continue;
                }

                if(text.contains("A program which provides day"))
                    System.out.println(text);

                if (isBlockStart(text)) {
                    inBlock = true;
                    blockBuffer.setLength(0);
                    saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                    continue;
                }

                if (isBlockEnd(text)) {
                    inBlock = false;
                    String blockText = blockBuffer.toString();
                    currentDescriptions.add("[BLOCK]\n" + blockText);
                    saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                    blockBuffer.setLength(0);
                    continue;
                }

                if (isListStart(text)) {
                    // finished with top element
                    saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                    currentDescriptions.clear();
                    phoneLines.clear();
                    addressLines.clear();
                    inBlock = false;
                    inList = true;
                    continue;
                }

                if (isListEnd(text)) {
                    inBlock = false;
                    inList = false;
                    saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                    resourceStack.pop();
                    descriptions.pop();
                    continue;
                }

                if (inBlock) {
                    StringBuilder formatted = new StringBuilder("<p>");

                    for (XWPFRun run : para.getRuns()) {
                        String runText = run.text();

                        if (runText != null && !runText.isBlank()) {
                            if (run.isBold()) formatted.append("<b>");
                            if (run.isItalic()) formatted.append("<i>");
                            formatted.append(runText.replace("\n", "<br/>"));  // preserve internal line breaks
                            if (run.isItalic()) formatted.append("</i>");
                            if (run.isBold()) formatted.append("</b>");
                        }
                    }

                    formatted.append("</p>\n");
                    blockBuffer.append(formatParagraphAsHtml(para)).append("\n");
                    continue;
                }

                if (looksLikeSubcategory(para)) {
                    if (activeResource.getType() == ResourceType.Resource) {
                        saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                        resourceStack.pop();
                        descriptions.pop();
                        items.push(para);
                        return;
                    }

                    if (activeResource.getType() == ResourceType.Subcategory) {
                        saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                        resourceStack.pop();
                        descriptions.pop();
                        items.push(para);
                        return;
                    }
                    if (activeResource.getType() == ResourceType.Category) {
                        saveResource(activeResource, currentDescriptions, phoneLines, addressLines);

                        CommunityResource current = insertResource(text, ResourceType.Subcategory, activeResource.getId());
                        resourceStack.push(current);
                        descriptions.push(new ArrayList<>());
                        //     items.next();
                        handleItems(items, resourceStack,descriptions, false);
                        continue;
                    }

                }

                if (looksLikeResource(para)) {

                    if (activeResource.getType() == ResourceType.Resource) {

                        if (inList) {
                            CommunityResource current = insertResource(text, ResourceType.Resource, activeResource.getId());
                            resourceStack.push(current);
                            descriptions.push(new ArrayList<>());
                            //       items.next();
                            handleItems(items, resourceStack,descriptions, false);
                            continue;
                        } else {
                            saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                            resourceStack.pop();
                            items.push(para);
                            return;

                        }
                    }
                    if (activeResource.getType() == ResourceType.Category || activeResource.getType() == ResourceType.Subcategory) {
                        saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                        currentDescriptions.clear();
                    }

                    CommunityResource current = insertResource(text, ResourceType.Resource, activeResource.getId());

                    resourceStack.push(current);
                    descriptions.push(new ArrayList<>());
                    handleItems(items, resourceStack,descriptions, inList);
                    continue;
                }

                // Main parsing logic
                if (activeResource != null) {
                    // If we're collecting description and hit a structured field, stop
                    if (readingDescription) {
                        if (PHONE_PATTERN.matcher(text).find() ||
                                EMAIL_PATTERN.matcher(text).find() ||
                                URL_PATTERN.matcher(text).find() ||
                                HoursHandler.isHours(text) ||
                                AddressHandler.isAddress(text)) {
                            readingDescription = false;
                        } else {
                            readDescription = true;
                            currentDescriptions.add(text);
                            continue;
                        }
                    }

                    // Handle address continuation
                    if (readingAddress) {
                        if (PHONE_PATTERN.matcher(text).find() ||
                                EMAIL_PATTERN.matcher(text).find() ||
                                URL_PATTERN.matcher(text).find() ||
                                HoursHandler.isHours(text) ) {
                            readingAddress = false;
                            // fall through to process as structured field
                        } else {
                            addressLines.add(text);
                            continue;
                        }
                    }
                    if (AddressHandler.isAddress(text) ) {
                        addressLines.add(text);
                        readingAddress = true;
                    } else if (PHONE_PATTERN.matcher(text).find()) {
                        phoneLines.add(text);
                    } else if (EMAIL_PATTERN.matcher(text).find()) {
                        String realEmail = text;
                        if (realEmail.startsWith("Email:")) {
                            realEmail = realEmail.replace("Email:", "");
                            realEmail = realEmail.trim();
                        }
                        activeResource.setEmail(realEmail);
                    } else if (URL_PATTERN.matcher(text).find()) {
                        activeResource.setWebsite(text);
                    } else if (HoursHandler.isHours(text)) {
                        activeResource.setHours(text);
                    } else {
                        if (!readDescription)
                            currentDescriptions.add(text); // fallback if unmatched
                        else {
                            if (addressLines.size() == 0) {
                                addressLines.add(text);
                                readingAddress = true;
                            }
                        }
                    }
                } else {
                    // Category-level description
                    ResourceDescriptionDAO.insert(new ResourceDescription(activeResource.getId(), text, false));
                }
            }
            if(!resourceStack.isEmpty()) {
                activeResource = resourceStack.lastElement();
                // Final resource save
                saveResource(activeResource, currentDescriptions, phoneLines, addressLines);
                resourceStack.pop();
                descriptions.pop();
            }
        }
        finally {
            //         System.out.println("finished " + activeResource.getName());
            while(!resourceStack.isEmpty()) {
                int activeId = resourceStack.lastElement().getId();
                if(activeId != activeResource.getId())
                    break;
                resourceStack.pop();
                descriptions.pop();
            }
        }
    }

    private static void printResourceStack(Stack<CommunityResource> resourceStack) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < resourceStack.size(); i++) {
            sb.append(resourceStack.get(i).getName());
            sb.append("-->");
        }
        System.out.println(sb.toString());
    }


    private static void saveResource(CommunityResource resource, List<String> descriptions,
                                     List<String> phoneLines, List<String> addressLines) throws Exception {
        int id = resource.getId();
        String name = resource.getName();
        //       if(id == 2)
        //           System.out.println(name);

        for (String desc : descriptions) {
            boolean isBlock  = false;
            if(desc.startsWith("[BLOCK]"))  {
                desc = desc.substring("[BLOCK]".length(), desc.length());
                isBlock = true;
            }
            ResourceDescriptionDAO.insert(new ResourceDescription(id , desc, isBlock));
        }
        descriptions.clear();
        if(savedItems.contains(id))
            return;
        savedItems.add(resource.getId());
        //     System.out.println("Saving " + name);
        if (resource != null) {
            if (!phoneLines.isEmpty()) {
                resource.setPhone(String.join("\n", phoneLines));
            }
            if (!addressLines.isEmpty()) {
                String join = String.join("\n", addressLines);
                resource.setAddress(join);
            }
             CommunityResourceDAO.update(resource);
            

            if (resource.hasSiteInfo()) {
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

    private static String formatParagraphAsHtml(XWPFParagraph para) {
        StringBuilder html = new StringBuilder("<p>");

        for (XWPFRun run : para.getRuns()) {
            String text = run.text();
            if (text == null || text.isBlank()) continue;

            if (run.isBold()) html.append("<b>");
            if (run.isItalic()) html.append("<i>");

            html.append(text.replace("\n", "<br/>")); // preserve soft breaks

            if (run.isItalic()) html.append("</i>");
            if (run.isBold()) html.append("</b>");
        }

        html.append("</p>");
        return html.toString();
    }

    private static boolean isBlockStart(String text) {
        if ("[START_BLOCK]".equals(text)) {
            return true;
        }
        if ("[BLOCK_START]".equals(text)) {
            return true;
        }
        return false;
    }

    private static boolean isBlockEnd(String text) {
        if ("[END_BLOCK]".equals(text)) {
            return true;
        }
        if ("[BLOCK_END]".equals(text)) {
            return true;
        }
        return false;
    }

    private static boolean isListStart(String text) {
        if ("[START_LIST]".equals(text)) {
            return true;
        }
        if ("[LIST_START]".equals(text)) {
            return true;
        }
        return false;
    }

    private static boolean isListEnd(String text) {
        if ("[END_LIST]".equals(text)) {
            return true;
        }
        if ("[LIST_END]".equals(text)) {
            return true;
        }
        return false;
    }

    private static class ParagraphIterator implements Iterator<XWPFParagraph> {
        int index = -1;
        List<XWPFParagraph> paragraphs;

        ParagraphIterator(List<XWPFParagraph> paragraphs) {
            this.paragraphs = paragraphs;
        }

        public XWPFParagraph current() {
            return paragraphs.get(index);
        }

        public XWPFParagraph next() {
            index++;
            return current();
        }

        public void push(XWPFParagraph paragraph) {
            index--;
        }

        @Override
        public boolean hasNext() {
            return index < paragraphs.size() - 1;
        }
    }
}
