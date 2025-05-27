package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Resource;
import com.lordjoe.resource_guide.SubCatagory;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.util.BlockHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.awt.*;
import java.util.List;

public class CategoryPageGenerator {

    public static String generateCategoryPage(Catagory category) {
        Color color = ColorMap.getColor(category.getName());
        String hexColor = colorToHex(color);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());

        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("<style>");

        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f9f9f9; }");

        html.append(".category-header { ");
        html.append("  position: sticky; top: 0; z-index: 1000; background-color: " + hexColor + "; ");
        html.append("  padding: 20px; color: white; display: flex; justify-content: space-between; ");
        html.append("  align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }");

        html.append(".home-button { background: white; color: black; padding: 10px 20px; ");
        html.append("  border-radius: 5px; text-decoration: none; font-weight: bold; }");

        html.append(".description { font-style: italic; margin: 10px 20px; }");

        html.append(".category-divider { border: 2px solid " + hexColor + "; margin: 20px; }");

        html.append(".anchor-menu { margin: 10px 20px; padding: 10px; background: #fff3cd; ");
        html.append("  border-left: 5px solid " + hexColor + "; font-size: 14px; }");

        html.append(".anchor-menu a { margin-right: 10px; text-decoration: none; ");
        html.append("  color: #333; font-weight: bold; }");

        html.append(".grid-container { display: grid; grid-template-columns: repeat(2, 1fr); ");
        html.append("  gap: 20px; margin: 20px; }");

        html.append(".grid-item { background: white; padding: 20px; border-radius: 8px; ");
        html.append("  box-shadow: 0 2px 8px rgba(0,0,0,0.1); }");

        html.append(".grid-item h2 { margin-top: 0; }");

        html.append(".subcategory-header { background-color: " + hexColor + "; color: white; ");
        html.append("  padding: 10px 14px; font-size: 16px; font-weight: bold; border-radius: 4px; ");
        html.append("  grid-column: span 2; margin-top: 40px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }");

        html.append(".block-container { margin: 30px 20px; padding: 20px; background: #fff9e6; ");
        html.append("  border-left: 6px solid " + hexColor + "; border-radius: 8px; ");
        html.append("  box-shadow: 0 2px 5px rgba(0,0,0,0.05); }");

        html.append(".block-html p { margin: 0 0 1em 0; line-height: 1.6; }");

        html.append(".block-html b { font-weight: bold; }");

        html.append(".block-html { font-family: inherit; font-size: 1em; line-height: 1.6; color: black; }");

        html.append("</style>");
        html.append("</head><body>");

        html.append("<div class=\"category-header\">\n<h1>").append(escapeHtml(category.getName())).
                append("</h1><a href=\"/\" class=\"home-button\">Home</a> ");
        if (isAuthenticated) {
            html.append("<div class=\"category-buttons\" style=\"margin-top:10px;\">\n");
            html.append("<form action=\"/edit-resource\" method=\"get\" style=\"margin-bottom: 20px;\">");
            html.append("<input type=\"hidden\" name=\"id\" value=\"0\" />"); // <-- Add this line
            html.append("<input type=\"hidden\" name=\"parentId\" value=\"").append(category.getId()).append("\" />");
            html.append("<button type=\"submit\" class=\"button\" style=\"background-color: #4caf50;\">New Resource</button>");
            html.append("</form>");

            html.append("  <form action=\"/new-subcategory\" method=\"get\"  style=\"margin-bottom: 20px;\"\">\n");
            html.append("    <input type=\"hidden\" name=\"parentId\" value=\"" + category.getId() + "\" />\n");
            html.append("    <button class=\"button\" type=\"submit\">Add New Subcategory</button>\n");
            html.append("  </form>\n");
            html.append("</div>\n");
        }
        html.append("</div>\n");

        List<SubCatagory> subcategories = category.getSubCatagories();
        if (!subcategories.isEmpty()) {
            html.append("<div class=\"anchor-menu\">Jump to:");
            for (SubCatagory sub : subcategories) {
                html.append(" <a href=\"#subcat-" + sub.getName().hashCode() + "\">" + escapeHtml(sub.getName()) + "</a>");

            }
            html.append("</div>");
        }

        html.append("<hr class=\"category-divider\" style=\"border-color: " + hexColor + ";\">");
        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            html.append("<div class=\"description\">" + escapeHtml(category.getDescription()) + "</div>");
        }

        if (!category.getBlocks().isEmpty()) {
            for (Resource res : category.getBlocks()) {
                html.append("<div class=\"block-container\">\n");

                String block = res.getDescription(); // already valid HTML
                html.append("<div class=\"block-html\">").append(block).append("</div>\n");

                html.append("</div>\n");
            }
        }
        html.append("<div class=\"grid-container\">");

        for (Resource r : category.getResources()) {
            if (r.getName().equals(BlockHandler.BLOCK_NAME) || r.getType() == ResourceType.Block) {
                html.append("<div class=\"block-container\">\n");
                html.append("<div class=\"block-html\">").append(r.getDescription()).append("</div>\n");
                html.append("</div>\n");
            } else {
                appendResourceHtml(r, html, isAuthenticated);
            }
        }

        for (SubCatagory sub : subcategories) {
            html.append("<div id=\"subcat-" + sub.getName().hashCode() + "\" class=\"subcategory-header\">" + escapeHtml(sub.getName()));
            if (isAuthenticated) {
                html.append("<div class=\"category-buttons\" style=\"margin-top:10px;\">\n");
                html.append("<form action=\"/edit-resource\" method=\"get\" style=\"margin-bottom: 10px;\">");
                html.append("<input type=\"hidden\" name=\"id\" value=\"0\" />"); // <-- Add this line
                html.append("<input type=\"hidden\" name=\"parentId\" value=\"").append(sub.getId()).append("\" />");
                html.append("<button type=\"submit\" class=\"button\" style=\"background-color: #4caf50;\">New Resource</button>");
                html.append("</form>");
                html.append("</div>\n");
            }
            // 🔽 ADD THIS SECTION
            if (!sub.getBlocks().isEmpty()) {
                for (Resource res : sub.getBlocks()) {
                    html.append("<div class=\"block-container\">\n");
                    String block = res.getDescription(); // already valid HTML
                    html.append("<div class=\"block-html\">").append(block).append("</div>\n");
                    html.append("</div>\n");
                }
            }

                 html.append("</div>\n");
                for (Resource r : sub.getResources()) {
                    if (r.getName().equals(BlockHandler.BLOCK_NAME) || r.getType() == ResourceType.Block) {
                        if(false) {

                            html.append("<div class=\"block-container\">\n");
                            html.append("<div class=\"block-html\">").append(r.getDescription()).append("</div>\n");
                            html.append("</div>\n");
                        }
                    } else {
                        appendResourceHtml(r, html, isAuthenticated);
                    }
                }
             }

        

        html.append("</div></body></html>");
        return html.toString();
    }

    private static void appendResourceHtml(Resource r, StringBuilder html, boolean isAuthenticated) {

        if (r.getName().equals(BlockHandler.BLOCK_NAME) || r.getType() == ResourceType.Block)
            return;

        html.append("<div class=\"grid-item\">\n<h2>").append(escapeHtml(r.getName())).append("</h2>");

        if (r.getDescription() != null) html.append("<p>" + escapeHtml(r.getDescription()) + "</p>");
        if (r.getPhone() != null) html.append("<p><strong>Phone:</strong> " + escapeHtml(r.getPhone()) + "</p>");
        if (r.getEmail() != null) html.append("<p><strong>Email:</strong> " + escapeHtml(r.getEmail()) + "</p>");
        if (r.getHours() != null)
            html.append("<p><strong>Hours:</strong> " + escapeHtml(r.getHours()) + "</p>");
        if (r.getWebsite() != null) {
            String[] urls = r.getWebsite().split(",");
            for (String url : urls) {
                html.append("<p><strong>Website:</strong> <a href='" + escapeHtml(url.trim()) + "' target='_blank' rel='noopener noreferrer'>" + escapeHtml(url.trim()) + "</a></p>");
            }
        }
        if (r.getAddress() != null) {
            html.append("<p><strong>Address:</strong> " + escapeHtml(r.getAddress()) + "</p>");
        }

        if (isAuthenticated) {

            html.append("<form method='get' action='/edit-resource'>")
                    .append("<input type='hidden' name='id' value='").append(r.getId()).append("'/>")
                    .append("<button type='submit' style='margin-top:10px;'>Edit</button>")
                    .append("</form>");
        }

        html.append("</div>");
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
