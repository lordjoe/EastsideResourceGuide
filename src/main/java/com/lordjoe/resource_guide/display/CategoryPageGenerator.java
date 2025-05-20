package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.*;
import com.lordjoe.resource_guide.util.BlockHandler;

import java.awt.*;
import java.util.List;

public class CategoryPageGenerator {

    public static String generateCategoryPage(Catagory category) {
        Color color = ColorMap.getColor(category.getName());
        String hexColor = colorToHex(color);

        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("  <link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">\n");
        html.append("  <title>Eastside Resource Guide</title>\n");
        html.append("<style>");
        html.append("    font-family: Arial, sans-serif; ");
        html.append("    margin: 0; ");
        html.append("    padding: 0; ");
        html.append("    background: #f9f9f9; ");
        html.append("} ");

        html.append(".category-header { ");
        html.append("  position: sticky; ");
        html.append("  top: 0; ");
        html.append("  z-index: 1000; ");
        html.append("  padding: 20px; ");
        html.append("  display: flex; ");
        html.append("  justify-content: space-between; ");
        html.append("  align-items: center; ");
        html.append("  box-shadow: 0 2px 5px rgba(0,0,0,0.2); ");
        html.append("  color: white; ");
        html.append("} ");

        html.append(".category-header h1 { ");
        html.append("  margin: 0; ");
        html.append("  font-size: 28px; ");
        html.append("} ");

        html.append(".home-button { ");
        html.append("  background: white; ");
        html.append("  color: black; ");
        html.append("  padding: 10px 20px; ");
        html.append("  border-radius: 5px; ");
        html.append("  text-decoration: none; ");
        html.append("  font-weight: bold; ");
        html.append("  font-size: 16px; ");
        html.append("} ");

  
        html.append(".description { ");
        html.append("    font-style: italic; ");
        html.append("    margin: 10px 20px; ");
        html.append("} ");

        html.append(".category-divider { ");
        html.append("    border: 2px solid #ccc; "); // overridden dynamically
        html.append("    margin: 20px; ");
        html.append("} ");

        html.append(".anchor-menu { ");
        html.append("    margin: 10px 20px; ");
        html.append("    padding: 10px; ");
        html.append("    background: #fff3cd; ");
        html.append("    border-left: 5px solid #ffa000; ");
        html.append("    font-size: 14px; ");
        html.append("} ");

        html.append(".anchor-menu a { ");
        html.append("    margin-right: 10px; ");
        html.append("    text-decoration: none; ");
        html.append("    color: #333; ");
        html.append("    font-weight: bold; ");
        html.append("} ");

        html.append(".grid-container { ");
        html.append("    display: grid; ");
        html.append("    grid-template-columns: repeat(2, 1fr); ");
        html.append("    gap: 20px; ");
        html.append("    margin: 20px; ");
        html.append("} ");

        html.append("@media screen and (max-width: 900px) { ");
        html.append("    .grid-container { grid-template-columns: repeat(2, 1fr); } ");
        html.append("} ");

        html.append(".grid-item { ");
        html.append("    background: white; ");
        html.append("    padding: 20px; ");
        html.append("    border-radius: 8px; ");
        html.append("    box-shadow: 0 2px 8px rgba(0,0,0,0.1); ");
        html.append("} ");

        html.append(".grid-item h2 { ");
        html.append("    margin-top: 0; ");
        html.append("} ");

        html.append(".subcategory-header { ");
        html.append("    background-color: #3f51b5; "); // overridden dynamically
        html.append("    color: white; ");
        html.append("    padding: 10px 14px; ");
        html.append("    font-size: 16px; ");
        html.append("    font-weight: bold; ");
        html.append("    border-radius: 4px; ");
        html.append("    grid-column: span 2; ");
        html.append("    margin-top: 40px; ");
        html.append("    box-shadow: 0 1px 3px rgba(0,0,0,0.1); ");
        html.append("} ");

        html.append(".block-html p { ");
        html.append("    margin: 0 0 1em 0; ");
        html.append("    line-height: 1.5; ");
        html.append("} ");

        html.append(".block-html { ");
        html.append("    font-family: inherit; ");
        html.append("} ");

        html.append(".child-entries { ");
        html.append("    margin-top: 12px; ");
        html.append("    padding-left: 16px; ");
        html.append("    border-left: 2px solid #ccc; ");
        html.append("} ");

        html.append(".child-entry { ");
        html.append("    background-color: #f4f4f4; ");
        html.append("    margin-bottom: 10px; ");
        html.append("    padding: 8px 12px; ");
        html.append("    border-radius: 6px; ");
        html.append("    font-size: 0.9em; ");
        html.append("    line-height: 1.3em; ");
        html.append("    box-shadow: 0 1px 2px rgba(0,0,0,0.05); ");
        html.append("} ");

        html.append(".child-entry h3 { ");
        html.append("    margin: 0 0 4px 0; ");
        html.append("    font-size: 1.05em; ");
        html.append("} ");

        html.append(".category-header {\n" +
                "    position: sticky;\n" +
                "    top: 0;\n" +
                "    z-index: 1000;\n" +
                "    padding: 20px;\n" +
                "    color: white;\n" +
                "    display: flex;\n" +
                "    justify-content: space-between;\n" +
                "    align-items: center;\n" +
                "    box-shadow: 0 2px 5px rgba(0,0,0,0.2);\n" +
                "}");
        
        html.append("</style>");

        html.append("</head><body>");

        html.append("<div class=\"category-header\" style=\"background-color: ")
                .append(hexColor).append(";\">\n");
        html.append("<h1>").append(escapeHtml(category.getName())).append("</h1>\n");
        html.append("<a href=\"/\" class=\"home-button\">Home</a>\n");
        html.append("</div>\n");

        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            html.append("<div class=\"description\">" + escapeHtml(category.getDescription()) + "</div>");
        }

        if (!category.getBlocks().isEmpty()) {
            for (Resource res : category.getBlocks()) {
                html.append("<div class=\"grid-item\" style=\"grid-column: span 2; background: #fff9e6; border-left: 5px solid ")
                        .append(hexColor).append("; padding: 20px;\">\n");
                String block = res.getDescription(); // already HTML
                html.append("<div class=\"block-html\">").append(block).append("</div>");
                html.append("</div>");
            }
        }

        List<SubCatagory> subcategories = category.getSubCatagories();
        if (!subcategories.isEmpty()) {
            html.append("<div class=\"anchor-menu\">Jump to:");
            for (SubCatagory sub : subcategories) {
                html.append(" <a href=\"#subcat-" + sub.getName().hashCode() + "\">" + escapeHtml(sub.getName()) + "</a>");
            }
            html.append("</div>");
        }

        html.append("<hr class=\"category-divider\" style=\"border-color: " + hexColor + ";\">");
        html.append("<div class=\"grid-container\">");

        for (Resource r : category.getResources()) {
            appendResourceHtml(r, html);
        }

        for (SubCatagory sub : subcategories) {
            html.append("<div id=\"subcat-" + sub.getName().hashCode() + "\" class=\"subcategory-header\">" + escapeHtml(sub.getName()) + "</div>");
            for (Resource r : sub.getResources()) {
                appendResourceHtml(r, html);
            }
        }

        html.append("</div></body></html>");
        return html.toString();
    }

    private static void appendResourceHtml(Resource r, StringBuilder html) {
        if (r.getName().equals(BlockHandler.BLOCK_NAME)) return;

        html.append("<div class=\"grid-item\">\n<h2>").append(escapeHtml(r.getName())).append("</h2>\n");

        // Show all parent resource fields
        appendResourceFields(r, html);

        // Show children inline, indented
        if (r.hasChildren()) {
            html.append("<div class=\"child-entries\">\n");
            for (Resource child : r.getResources()) {
                html.append("<div class=\"child-entry\">\n");
                html.append("<h3>").append(escapeHtml(child.getName())).append("</h3>\n");
                appendResourceFields(child, html);
                html.append("</div>\n");
            }
            html.append("</div>\n");
        }

        html.append("</div>\n");
    }

    private static void appendResourceFields(Resource r, StringBuilder html) {
        if (r.getDescription() != null && !r.getDescription().isBlank())
            html.append("<p>").append(escapeHtml(r.getDescription())).append("</p>\n");

        if (r.getPhone() != null && !r.getPhone().isBlank())
            html.append("<p><strong>Phone:</strong> ").append(escapeHtml(r.getPhone())).append("</p>\n");

        if (r.getEmail() != null && !r.getEmail().isBlank())
            html.append("<p><strong>Email:</strong> ").append(escapeHtml(r.getEmail())).append("</p>\n");

        if (r.getHours() != null && !r.getHours().isBlank())
            html.append("<p><strong>Hours:</strong> ").append(escapeHtml(r.getHours())).append("</p>\n");

        if (r.getWebsite() != null && !r.getWebsite().isBlank()) {
            for (String url : r.getWebsite().split(",")) {
                html.append("<p><strong>Website:</strong> <a href='").append(escapeHtml(url.trim()))
                        .append("' target='_blank'>").append(escapeHtml(url.trim())).append("</a></p>\n");
            }
        }

        if (r.getAddress() != null && !r.getAddress().isBlank())
            html.append("<p><strong>Address:</strong> ").append(escapeHtml(r.getAddress())).append("</p>\n");
    }


    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String colorToHex(Color color) {
        if (color == null) return "#cccccc";
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
