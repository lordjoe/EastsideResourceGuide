package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.model.CommunityResource;

public class EditResourcePageGenerator {

    public static String generateEditPage(CommunityResource resource) {
        StringBuilder html = new StringBuilder();
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>Edit Resource</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; }\n");
        html.append("        h1 { text-align: center; color: #3f51b5; }\n");
        html.append("        form { max-width: 700px; margin: auto; display: flex; flex-direction: column; gap: 12px; background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }\n");
        html.append("        label { font-weight: bold; margin-top: 10px; }\n");
        html.append("        input[type=text], textarea { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; }\n");
        html.append("        textarea { height: 150px; resize: vertical; }\n");
        html.append("        .button-group { display: flex; justify-content: space-between; gap: 10px; margin-top: 20px; }\n");
        html.append("        button { padding: 12px 20px; background-color: #3f51b5; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }\n");
        html.append("        button:hover { background-color: #2c3f91; }\n");
        html.append("        .delete-form { max-width: 700px; margin: auto; text-align: right; margin-top: 10px; }\n");
        html.append("        .cancel-button { background-color: #9e9e9e; }\n");
        html.append("        .website-link { font-size: 14px; margin-top: 5px; }\n");
        html.append("    </style>\n");
        html.append("    <script>\n");
        html.append("        function goBack() { window.history.back(); }\n");
        html.append("    </script>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <h1>Edit Resource</h1>\n");
        html.append("    <form method=\"post\" action=\"/update-resource\">\n");
        html.append("        <input type=\"hidden\" name=\"id\" value=\"" + resource.getId() + "\"/>\n");

        html.append("        <label for=\"name\">Name:</label>\n");
        html.append("        <input type=\"text\" id=\"name\" name=\"name\" value=\"" + escape(resource.getName()) + "\" required />\n");

        html.append("        <label for=\"description\">Description:</label>\n");
        html.append("        <textarea id=\"description\" name=\"description\">" + escape(resource.getDescription()) + "</textarea>\n");

        html.append("        <label for=\"phone\">Phone:</label>\n");
        html.append("        <input type=\"text\" id=\"phone\" name=\"phone\" value=\"" + escape(resource.getPhone()) + "\" />\n");

        html.append("        <label for=\"email\">Email:</label>\n");
        html.append("        <input type=\"text\" id=\"email\" name=\"email\" value=\"" + escape(resource.getEmail()) + "\" />\n");


        html.append("<label for='website'>Website:</label>");
        html.append("<input type='text' id='website' name='website' value='").append(escape(resource.getWebsite())).append("' />");
        if (resource.getWebsite() != null && !resource.getWebsite().trim().isEmpty()) {
            html.append("<div class='website-link'><a href='").append(escape(resource.getWebsite())).append("' target='_blank' rel='noopener noreferrer'>Open Website</a></div>");
        }


        html.append("        <label for=\"address\">Address:</label>\n");
        html.append("        <input type=\"text\" id=\"address\" name=\"address\" value=\"" + escape(resource.getAddress()) + "\" />\n");

        html.append("        <label for=\"hours\">Hours:</label>\n");
        html.append("        <input type=\"text\" id=\"hours\" name=\"hours\" value=\"" + escape(resource.getHours()) + "\" />\n");

        html.append("        <label for=\"notes\">Notes:</label>\n");
        html.append("        <input type=\"text\" id=\"notes\" name=\"notes\" value=\"" + escape(resource.getNotes()) + "\" />\n");

        html.append("        <div class=\"button-group\">\n");
        html.append("            <button type=\"submit\">Save</button>\n");
        html.append("            <button type=\"button\" class=\"cancel-button\" onclick=\"goBack()\">Cancel</button>\n");
        html.append("        </div>\n");
        html.append("    </form>\n");

        html.append("    <form method=\"post\" action=\"/delete-resource\" onsubmit=\"return confirm('Are you sure you want to delete this resource?');\" class=\"delete-form\">\n");
        html.append("        <input type=\"hidden\" name=\"id\" value=\"" + resource.getId() + "\" />\n");
        html.append("        <button type=\"submit\" style=\"background-color: #e53935;\">Delete</button>\n");
        html.append("    </form>\n");
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
