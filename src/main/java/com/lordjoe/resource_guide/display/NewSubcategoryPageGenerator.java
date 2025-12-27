package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;

public class NewSubcategoryPageGenerator {

    public static String generateNewSubcategoryPage(int parentId) {
        Catagory category = Guide.Instance.getCatagoryById(parentId);

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>New Subcategory</title>");
        html.append("<link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">");
        html.append("<style>");
        html.append("body { background-image: url('/Cover.png'); background-size: cover; ")
                .append("font-family: Arial, sans-serif; padding: 40px; }");
        html.append(".form-container { background-color: rgba(255,255,255,0.95); ")
                .append("max-width: 600px; margin: 0 auto; padding: 20px; border-radius: 10px; ")
                .append("box-shadow: 0 4px 8px rgba(0,0,0,0.2); }");
        html.append("label { display: block; margin-top: 10px; font-weight: bold; }");
        html.append("input[type='text'], textarea { width: 100%; padding: 8px; ")
                .append("margin-top: 5px; border-radius: 4px; border: 1px solid #ccc; }");
        html.append(".buttons { margin-top: 20px; display: flex; gap: 10px; }");
        html.append(".save-btn { background-color: #4caf50; color: white; border: none; ")
                .append("padding: 10px 20px; border-radius: 4px; cursor: pointer; }");
        html.append(".cancel-btn { background-color: #f44336; color: white; border: none; ")
                .append("padding: 10px 20px; border-radius: 4px; cursor: pointer; }");
        html.append("</style></head><body>");

        html.append("<div class='form-container'>");
        html.append("<h2>Create New Subcategory under ")
                .append(category != null ? escapeHtml(category.getName()) : "Unknown")
                .append("</h2>");

        // IMPORTANT: plain POST, no JS gate, Save always submits
        html.append("<form method='post' action='/create-subcategory'>");
        html.append("<input type='hidden' name='parentId' value='").append(parentId).append("'>");

        html.append("<label for='name'>Subcategory Name:</label>");
        html.append("<input type='text' id='name' name='name' required>");

        html.append("<label for='description'>Description (optional):</label>");
        html.append("<textarea id='description' name='description' rows='4' cols='50'></textarea>");

        html.append("<div class='buttons'>");
        html.append("<button type='submit' class='save-btn'>Save</button>");
        html.append("<button type='button' class='cancel-btn' onclick='history.back()'>Cancel</button>");
        html.append("</div>");

        html.append("</form></div>");
        html.append("</body></html>");

        return html.toString();
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
