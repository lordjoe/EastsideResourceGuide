package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NewSubcategoryController {

    @GetMapping("/new-subcategory")
    @ResponseBody
    public String newSubcategoryForm(@RequestParam("parentId") int parentId) {
           return NewSubcategoryPageGenerator.generateNewSubcategoryPage(parentId);
    }

    @PostMapping("/new-subcategory")
    public String createSubcategory(@RequestParam("parentId") int parentId,
                                    @RequestParam("name") String name) throws Exception {
        CommunityResource subcat = new CommunityResource(name, ResourceType.Subcategory, parentId);
        CommunityResourceDAO.insert(subcat);
 
        return "redirect:/category?category=" + Guide.Instance.getCatagoryById(parentId).getName();
    }
}
