package com.lordjoe.resource_guide.util;

import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceDescriptionDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.sql.SQLException;
import java.util.List;

public class BlockHandler {
    public static final String BLOCK_NAME = "<BLOCK>";

    public static void handleBlock(int ownerId, List<XWPFParagraph> block) {
        try {
            StringBuilder html = new StringBuilder();

            for (XWPFParagraph para : block) {
                html.append("<p>");

                List<XWPFRun> runs = para.getRuns();
                if (runs != null) {
                    for (XWPFRun run : runs) {
                        String text = run.text();
                        if (text == null || text.isEmpty()) continue;

                        if (run.isBold()) html.append("<b>");
                        if (run.isItalic()) html.append("<i>");
                        if (run.getUnderline() != null && run.getUnderline().getValue() > 0) html.append("<u>");

                        html.append(escapeHtml(text));

                        if (run.getUnderline() != null && run.getUnderline().getValue() > 0) html.append("</u>");
                        if (run.isItalic()) html.append("</i>");
                        if (run.isBold()) html.append("</b>");
                    }
                }

                html.append("</p>\n");
            }

            String htmlBlock = html.toString().trim();
            CommunityResource holder =  new CommunityResource();
            holder.setParentId(ownerId);
            holder.setType(ResourceType.Block);
            holder.setName(BLOCK_NAME);
            int holderId =  CommunityResourceDAO.insert(holder);
            ResourceDescription blockDesc = new ResourceDescription(holderId, htmlBlock, true);
            ResourceDescriptionDAO.insert(blockDesc);
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
