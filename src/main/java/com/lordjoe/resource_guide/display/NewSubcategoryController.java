package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.SubCatagory;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceDescriptionDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.model.ResourceDescription;
import com.lordjoe.resource_guide.util.DatabaseConnection;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Controller
public class NewSubcategoryController {

    @GetMapping("/create-subcategory")
    @ResponseBody
    public String newSubcategoryForm(@RequestParam("parentId") int parentId) {
        return NewSubcategoryPageGenerator.generateNewSubcategoryPage(parentId);
    }

    @PostMapping("/create-subcategory")
    public String createSubcategory(@RequestParam("parentId") int parentId,
                                    @RequestParam("name") String name,
                                    @RequestParam(value = "description", required = false) String description) throws Exception {
        Guide guide = Guide.Instance;
        guide.guaranteeLoaded();   // make sure in-memory model is populated

        // Look up parent category
        Catagory parent = guide.getCatagoryById(parentId);
        if (parent == null) {
            // Nothing to attach to – bail out safely
            return "redirect:/main";
        }

        // Normalize / guard name
        String trimmedName = (name == null) ? "" : name.trim();
        if (trimmedName.isEmpty()) {
            // No valid name -> just go back to category
            return "redirect:/category?category=" +
                    URLEncoder.encode(parent.getName(), StandardCharsets.UTF_8);
        }

        // At this point, the JS on the page already prevented duplicate names
        // within this category, so we do NOT block on getCatagoryByName here.
        CommunityResource subcr = CommunityResourceDAO.create(
                0,
                trimmedName,
                ResourceType.Subcategory,
                parentId
        );
        int subId = CommunityResourceDAO.insert(subcr);

        // Optional description
        if (description != null && !description.isEmpty()) {
            ResourceDescription des = new ResourceDescription(subId, description, false);
            ResourceDescriptionDAO.insert(des);
        }

        // Update in-memory Guide model so the new subcategory appears immediately
        SubCatagory sub = new SubCatagory(subId, trimmedName, parent);
        sub.setDescription(description);
        parent.addSubCatagory(sub);

        // Redirect back to the parent category page
        return "redirect:/category?category=" +
                URLEncoder.encode(parent.getName(), StandardCharsets.UTF_8);
    }

    @PostMapping("/deleteSubcategory")
    public void deleteSubcategory(@RequestParam("subcategoryId") int id,
                                  HttpServletResponse response) throws Exception {
        Guide guide = Guide.Instance;
        guide.guaranteeLoaded();

        SubCatagory subCat = guide.getSubCatagoryById(id);
        if (subCat != null && subCat.getResources().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt =
                         conn.prepareStatement("DELETE FROM community_resources WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            guide.reload(); // or update Guide in a more granular way if you prefer
        }

        String parentName = (subCat != null && subCat.getCatagory() != null)
                ? subCat.getCatagory().getName()
                : "";
        if (parentName.isEmpty()) {
            response.sendRedirect("/main");
        } else {
            response.sendRedirect("/category?category=" +
                    URLEncoder.encode(parentName, StandardCharsets.UTF_8));
        }
    }
}
