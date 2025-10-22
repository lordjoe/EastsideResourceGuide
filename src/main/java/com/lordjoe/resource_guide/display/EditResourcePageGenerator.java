package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Resource;
import com.lordjoe.resource_guide.util.URLValidator;

public class EditResourcePageGenerator {

    public static String generateEditPage( Resource resource) {
        boolean isNew = resource.getId() == 0;

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html><head>\n");
        html.append("    <title>").append(isNew ? "Create Resource" : "Edit Resource").append("</title>\n");
        html.append("    <link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">\n");
        html.append("    <style>\n");
        html.append("        body {\n");
        html.append("            background-image: url('/Cover.png');\n");
        html.append("            background-size: cover;\n");
        html.append("            background-position: center;\n");
        html.append("            font-family: Arial, sans-serif;\n");
        html.append("            margin: 0;\n");
        html.append("            padding: 20px;\n");
        html.append("        }\n");
        html.append("        .form-container {\n");
        html.append("            max-width: 600px;\n");
        html.append("            margin: 60px auto;\n");
        html.append("            padding: 30px;\n");
        html.append("            background-color: rgba(255,255,255,0.95);\n");
        html.append("            border-radius: 12px;\n");
        html.append("            box-shadow: 0 4px 8px rgba(0,0,0,0.2);\n");
        html.append("        }\n");
        html.append("        label { font-weight: bold; display: block; margin-top: 15px; }\n");
        html.append("        input[type='text'], textarea {\n");
        html.append("            width: 100%;\n");
        html.append("            padding: 10px;\n");
        html.append("            border: 1px solid #ccc;\n");
        html.append("            border-radius: 6px;\n");
        html.append("            box-sizing: border-box;\n");
        html.append("        }\n");
        html.append("        textarea { height: 100px; }\n");
        html.append("        .button-group {\n");
        html.append("            display: flex;\n");
        html.append("            justify-content: space-between;\n");
        html.append("            margin-top: 25px;\n");
        html.append("        }\n");
        html.append("        button {\n");
        html.append("            padding: 12px 24px;\n");
        html.append("            background-color: #3f51b5;\n");
        html.append("            color: white;\n");
        html.append("            font-size: 16px;\n");
        html.append("            border: none;\n");
        html.append("            border-radius: 6px;\n");
        html.append("            cursor: pointer;\n");
        html.append("        }\n");
        html.append("        button:hover { background-color: #2c3f91; }\n");
        html.append("    </style>\n</head>\n<body>\n");

        html.append("<div class=\"form-container\">\n");
        html.append("  <h2>").append(isNew ? "Create New Resource" : "Edit Resource").append("</h2>\n");
        html.append("  <form method=\"post\" action=\"/update-resource\">\n");

        html.append("    <input type=\"hidden\" name=\"id\" value=\"").append(resource.getId()).append("\" />\n");
        int parentId = resource.getParentId();
        html.append("    <input type=\"hidden\" name=\"parentId\" value=\"")
                .append(parentId )
                .append("\" />\n");

        html.append("    <label for=\"name\">Name:</label>\n");
        html.append("    <input type=\"text\" id=\"name\" name=\"name\" required value=\"")
                .append(escape(resource.getName()))
                .append("\" />\n");

        html.append("    <label for=\"description\">Description:</label>\n");
        html.append("    <textarea name=\"description\">")
                .append(escape(resource.getDescription()))
                .append("</textarea>\n");

        html.append("    <label for=\"phone\">Phone:</label>\n");
        html.append("    <input type=\"text\" name=\"phone\" value=\"")
                .append(escape(resource.getPhone()))
                .append("\" />\n");

        html.append("    <label for=\"email\">Email:</label>\n");
        html.append("    <input type=\"text\" name=\"email\" value=\"")
                .append(escape(resource.getEmail()))
                .append("\" />\n");

        html.append("    <label for=\"website\">Website:</label>\n");
        String website = resource.getWebsite() == null ? "" : resource.getWebsite();
        boolean valid = website.isEmpty() || URLValidator.isValidURL(website);
        if (!valid) {
            html.append("    <span style='color:red;'>Invalid URL</span><br/>\n");
        }
        html.append("    <input type=\"text\" name=\"website\" value=\"")
                .append(escape(website))
                .append("\" />\n");

        html.append("    <label for=\"address\">Address:</label>\n");
        html.append("    <input type=\"text\" name=\"address\" value=\"")
                .append(escape(resource.getAddress()))
                .append("\" />\n");

        html.append("    <label for=\"hours\">Hours:</label>\n");
        html.append("    <input type=\"text\" name=\"hours\" value=\"")
                .append(escape(resource.getHours()))
                .append("\" />\n");

        html.append("    <label for=\"notes\">Notes:</label>\n");
        html.append("    <input type=\"text\" name=\"notes\" value=\"")
                .append(escape(resource.getNotes()))
                .append("\" />\n");

        html.append("    <div class=\"button-group\">\n");
        html.append("      <button type=\"submit\">").append(isNew ? "Create" : "Save").append("</button>\n");

        if (!isNew) {
            html.append("      <button type=\"button\" onclick=\"window.history.back()\">Cancel</button>\n");
        }
        else {
            html.append("      <button type=\"button\" onclick=\"window.history.back()\">Cancel</button>\n");
        }
        html.append("    </div>\n");

        html.append("  </form>\n");

        if (!isNew) {
            html.append("  <form method=\"post\" action=\"/delete-resource\" ")
                    .append("onsubmit=\"return confirm('Are you sure you want to delete this resource?');\" ")
                    .append("style=\"margin-top: 10px;\">\n");
            html.append("    <input type=\"hidden\" name=\"id\" value=\"")
                    .append(resource.getId())
                    .append("\" />\n");
            html.append("    <button type=\"submit\" style=\"background-color: #d32f2f;\">Delete</button>\n");
            html.append("  </form>\n");
        }

        html.append("</div>\n</body></html>");

        return html.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
