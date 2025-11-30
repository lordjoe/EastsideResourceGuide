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
        html.append("body { background-image: url('/Cover.png'); background-size: cover; font-family: Arial, sans-serif; padding: 40px; }");
        html.append(".form-container { background-color: rgba(255, 255, 255, 0.95); padding: 30px; max-width: 500px; margin: auto; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); }");
        html.append("h2 { text-align: center; }");
        html.append("label { display: block; margin-top: 15px; font-weight: bold; }");
        html.append("input[type='text'] { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px; }");
        html.append(".buttons { margin-top: 20px; display: flex; justify-content: space-between; }");
        html.append("button { padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }");
        html.append(".save-btn { background-color: #4caf50; color: white; }");
        html.append(".cancel-btn { background-color: #ccc; color: black; }");
        html.append("</style>");
        html.append("<script>");
        html.append("function validateName() {");
        html.append("  const nameInput = document.getElementById('name');");
        html.append("  const saveBtn = document.getElementById('save-btn');");
        html.append("  const existingNames = [");
        for (int i = 0; i < category.getSubCatagories().size(); i++) {
            html.append('"').append(category.getSubCatagories().get(i).getName().replace("\"", "\\\"")).append('"');
            if (i < category.getSubCatagories().size() - 1) html.append(',');
        }
        html.append("];");
        html.append("  const name = nameInput.value.trim();");
        html.append("  saveBtn.disabled = name === '' || existingNames.includes(name);");
        html.append("}");
        html.append("</script></head><body>");

        html.append("<div class='form-container'>");
        html.append("<h2>Create New Subcategory</h2>");
        html.append("<form method='post' action='/create-subcategory'>");
        html.append("<input type='hidden' name='parentId' value='").append(parentId).append("'>");
        html.append("<label for='name'>Subcategory Name:</label>");
        html.append("<input type='text' id='name' name='name'   required>");
         html.append("<div class='buttons'>");
        html.append("<button type='submit' class='save-btn' id='save-btn' disabled>Save</button>");
        html.append("<button type='button' class='cancel-btn' onclick='history.back()'>Cancel</button>");
        html.append("</div></form></div>");

        html.append("</body></html>");
        return html.toString();
    }
}
