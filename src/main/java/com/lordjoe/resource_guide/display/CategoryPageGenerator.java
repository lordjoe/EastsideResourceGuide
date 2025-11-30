package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.*;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.util.URLValidator;
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
        if (LocalResourceGuidsApplication.DEBUG_ADMIN_MODE)
            isAuthenticated = true;

        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("<style>");

        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f9f9f9; }");
        html.append(".category-header { position: sticky; top: 0; z-index: 1000; background-color: " + hexColor + "; padding: 20px; color: white; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }");
        html.append(".home-button { background: white; color: black; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold; }");
        html.append(".description { font-style: italic; margin: 10px 20px; }");
        html.append(".category-divider { border: 2px solid " + hexColor + "; margin: 20px; }");
        html.append(".anchor-menu { margin: 10px 20px; padding: 10px; background: #fff3cd; border-left: 5px solid " + hexColor + "; font-size: 14px; }");
        html.append(".anchor-menu a { margin-right: 10px; text-decoration: none; color: #333; font-weight: bold; }");
        html.append(".grid-container { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin: 20px; }");
        html.append(".grid-item { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }");
        html.append(".grid-item h2 { margin-top: 0; }");
        html.append(".subcategory-header { background-color: " + hexColor + "; color: white; padding: 10px 14px; font-size: 16px; font-weight: bold; border-radius: 4px; grid-column: span 2; margin-top: 40px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }");
        html.append(".block-container { width: 100%; margin: 30px 0; padding: 20px; background: #fff9e6; border-left: 6px solid " + hexColor + "; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); grid-column: span 2; }");
        html.append(".block-html p { margin: 0 0 1em 0; line-height: 1.6; }");
        html.append(".block-html b { font-weight: bold; }");
        html.append(".block-html { font-family: inherit; font-size: 1em; line-height: 1.6; color: black; }");
        html.append(".child-list { margin-top: 15px; padding: 12px 16px; border-radius: 6px; background: #f5f7ff; border: 1px solid #e0e6f5; }");
        html.append(".child-list-title { font-weight: bold; margin-bottom: 8px; font-size: 0.95em; color: #555; }");
        html.append(".child-resource { margin-left: 0; padding: 8px 0; border-top: 1px solid #ddd; }");
        html.append(".child-resource:first-child { border-top: none; }");
        html.append(".child-resource h3 { margin-top: 0; font-size: 1.05em; }");

        html.append("</style>");
        html.append("</head><body>");

        html.append("<div class=\"category-header\">\n<h1>").append(escapeHtml(category.getName())).append("</h1><a href=\"/\" class=\"home-button\">Home</a> ");
        if (isAuthenticated) {
            html.append("<div class=\"category-buttons\" style=\"margin-top:10px;\">\n");
            html.append("<form action=\"/edit-resource\" method=\"get\" style=\"margin-bottom: 20px;\">");
            html.append("<input type=\"hidden\" name=\"id\" value=\"0\" />");
            html.append("<input type=\"hidden\" name=\"parentId\" value=\"").append(category.getId()).append("\" />");
            html.append("<button type=\"submit\" class=\"button\" style=\"background-color: #4caf50;\">New Resource</button>");
            html.append("</form>");
            html.append("<form action=\"/create-subcategory\" method=\"get\" style=\"margin-bottom: 20px;\">");
            html.append("<input type=\"hidden\" name=\"parentId\" value=\"").append(category.getId()).append("\" />");
            html.append("<button class=\"button\" type=\"submit\">Add New Subcategory</button>");
            html.append("</form></div>");
        }
        html.append("</div>");

        List<SubCatagory> subcategories = category.getSubCatagories();
        if (!subcategories.isEmpty()) {
            html.append("<div class=\"anchor-menu\">Jump to:");
            for (SubCatagory sub : subcategories) {
                html.append(" <a href=\"#subcat-").append(sub.getName().hashCode()).append("\">").append(escapeHtml(sub.getName())).append("</a>");
            }
            html.append("</div>");
        }

        html.append("<hr class=\"category-divider\" style=\"border-color: ").append(hexColor).append(";\">");
        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            String description = category.getDescription();
            html.append("<div class=\"description\">").append(escapeHtml(description)).append("</div>");
        }

        renderBlocks(html, category.getBlocks());

        html.append("<div class=\"grid-container\">");

        for (Resource r : category.getResources()) {
            if (r.getType() != ResourceType.Block)
                appendResourceHtml(r, html, isAuthenticated);
        }

        for (SubCatagory sub : subcategories) {
            html.append("<div id=\"subcat-").append(sub.getName().hashCode()).append("\" class=\"subcategory-header\">").append(escapeHtml(sub.getName()));

            if (isAuthenticated) {
                html.append("<div class=\"category-buttons\" style=\"margin: 10px 20px;\">");
                html.append("<form action=\"/edit-resource\" method=\"get\" style=\"margin-bottom: 10px;\">");
                html.append("<input type=\"hidden\" name=\"id\" value=\"0\" />");
                html.append("<input type=\"hidden\" name=\"parentId\" value=\"").append(sub.getId()).append("\" />");
                html.append("<button type=\"submit\" class=\"button\" style=\"background-color: #4caf50;\">New Resource</button>");
                html.append("</form>");

                List<Resource> resources = sub.getResources();

                boolean canDelete = resources.isEmpty();
                html.append("<form method='POST' action='/deleteSubcategory'>");
                html.append("<input type='hidden' name='subcategoryId' value='" + sub.getId() + "'/>");
                html.append("<button type='submit' " + (canDelete ? "" : "disabled") + ">Delete Subcategory</button>");
                html.append("</form>");
                html.append("</form></div>");
            }
            html.append("</div>");
            for (Resource r : sub.getResources()) {
                if (r.getType() != ResourceType.Block) {
                    appendResourceHtml(r, html, isAuthenticated);
                }
            }

            renderBlocks(html, sub.getBlocks());
        }

        html.append("</div></body></html>");
        return html.toString();
    }

    private static void renderBlocks(StringBuilder html, List<Resource> blocks) {
        for (Resource block : blocks) {
            html.append("<div class=\"block-container\">\n");
            String description = block.getDescription();
            html.append("<div class=\"block-html\">").append(description).append("</div>\n</div>\n");
        }
    }

    private static void appendResourceHtml(Resource r, StringBuilder html, boolean isAuthenticated) {
        if (r.getType() == ResourceType.Block)
            return;

        Guide guide = Guide.Instance;
        String name = r.getName();
        if (name.equals("Rename Me")) {
            guide = Guide.Instance; // debug hook
        }
        if (name.equals("King County Housing Authority (KCHA)")) {
            guide = Guide.Instance; // debug hook
        }

        html.append("<div class=\"grid-item\">\n");

        // Parent content in the main card
        renderResourceContent(r, html, isAuthenticated, "h2");

        // Children inside a separate inner box
        List<Resource> children = r.getResources();
        if (children != null && !children.isEmpty()) {
            html.append("<div class=\"child-list\">");
            // Optional label:
            // html.append("<div class=\"child-list-title\">Related properties:</div>");

            for (Resource child : children) {
                if (child.getType() == ResourceType.Resource) {
                    appendChildResourceHtml(child, html, isAuthenticated);
                }
            }

            html.append("</div>"); // end .child-list
        }

        html.append("</div>"); // end .grid-item
    }

    private static void appendChildResourceHtml(Resource r, StringBuilder html, boolean isAuthenticated) {
        if (r.getType() == ResourceType.Block)
            return;

        html.append("<div class=\"child-resource\">\n");
        renderResourceContent(r, html, isAuthenticated, "h3");
        html.append("</div>");

        // Grandchildren become more .child-resource blocks in the same child-list
        for (Resource child : r.getResources()) {
            if (child.getType() == ResourceType.Resource) {
                appendChildResourceHtml(child, html, isAuthenticated);
            }
        }
    }

    private static void renderResourceContent(Resource r, StringBuilder html, boolean isAuthenticated, String headingTag) {
        String name = r.getName();

        html.append("<").append(headingTag).append(">")
                .append(escapeHtml(name))
                .append("</").append(headingTag).append(">");

        if (r.getDescription() != null)
            html.append("<p>").append(escapeHtml(r.getDescription())).append("</p>");
        if (r.getPhone() != null)
            html.append("<p><strong>Phone:</strong> ").append(escapeHtml(r.getPhone())).append("</p>");
        if (r.getEmail() != null)
            html.append("<p><strong>Email:</strong> ").append(escapeHtml(r.getEmail())).append("</p>");
        if (r.getHours() != null)
            html.append("<p><strong>Hours:</strong> ").append(escapeHtml(r.getHours())).append("</p>");

        if (r.getWebsite() != null) {
            String[] urls = r.getWebsite().split(",");
            for (String url : urls) {
                url = url.replace("http://", "https://");
                if (!url.startsWith("https://"))
                    url = "https://" + url;
                url = url.replace(" ", "%20");

                boolean valid = true;
                if (isAuthenticated)
                    valid = URLValidator.isValidURL(url);

                if (valid) {
                    html.append("<p><strong>Website:</strong> ")
                            .append("<a href='")
                            .append(escapeHtml(url))
                            .append("' target='_blank' rel='noopener noreferrer'>")
                            .append(escapeHtml(url))
                            .append("</a></p>");
                } else {
                    // repeat to debug if you like
                    valid = URLValidator.isValidURL(url, true, false);
                    html.append("<p><strong>Website:</strong> ")
                            .append("<span style='color:red;'>")
                            .append("<a href='")
                            .append(escapeHtml(url))
                            .append("' target='_blank' rel='noopener noreferrer'>")
                            .append(escapeHtml(url))
                            .append("</a> (invalid)")
                            .append("</span></p>");
                }
            }
        }

        if (r.getAddress() != null) {
            html.append("<p><strong>Address:</strong> ")
                    .append(escapeHtml(r.getAddress()))
                    .append("</p>");
        }

        if (isAuthenticated) {
            html.append("<div style='display:flex; gap:10px; margin-top:10px;'>");

            html.append("<form method='get' action='/edit-resource' style='margin:0;'>")
                    .append("<input type='hidden' name='id' value='").append(r.getId()).append("'/>")
                    .append("<input type='hidden' name='parentId' value='").append(r.getParentId()).append("'/>")
                    .append("<button type='submit'>Edit</button>")
                    .append("</form>");

            html.append("<form method='post' action='/delete-resource' style='margin:0;'>")
                    .append("<input type='hidden' name='id' value='").append(r.getId()).append("'/>")
                    .append("<input type='hidden' name='parentId' value='").append(r.getParentId()).append("'/>")
                    .append("<button type='submit'>Delete</button>")
                    .append("</form>");

            html.append("</div>");
        }
    }


    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").
                replace(">", "&gt;");
    }

    private static String colorToHex(Color color) {
        if (color == null) return "#cccccc";
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
