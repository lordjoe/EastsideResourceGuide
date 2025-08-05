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
                                    @RequestParam("description") String description) throws Exception {
        CommunityResource subcat = new CommunityResource(name, ResourceType.Subcategory, parentId);
        int insert = CommunityResourceDAO.insert(subcat);

        if(description != null && description.length() > 0) {
            ResourceDescription des = new ResourceDescription(insert,  description, false);
            ResourceDescriptionDAO.insert(  des );
        }
        Guide instance = Guide.Instance;
        Catagory catagoryById = instance.getCatagoryById(parentId);
        SubCatagory subcat1 = new SubCatagory(insert, name, catagoryById);
        subcat1.setDescription(description);
        catagoryById.addSubCatagory(subcat1);
        return "redirect:/category?category=" +  URLEncoder.encode(catagoryById.getName(), StandardCharsets.UTF_8);
    }

    @PostMapping("/deleteSubcategory")
    public void deleteSubcategory(@RequestParam("subcategoryId") int id, HttpServletResponse response) throws Exception {
        Guide instance = Guide.Instance;
        SubCatagory subCat = instance.getSubCatagoryById(id);
        if (subCat != null && subCat.getResources().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM community_resources WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            instance.reload(); // or just remove subcategory from memory if you prefer
        }
        response.sendRedirect("/category?category=" + URLEncoder.encode(subCat.getCatagory().getName(), StandardCharsets.UTF_8));
    }
}
