package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Resource;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EditResourceController {

    @GetMapping("/edit-resource")
    @ResponseBody
    public String editResourcePage(@RequestParam(value = "id", required = false) Integer id,
                                   @RequestParam(value = "parentId", required = false) Integer parentId,
                                   Model model) throws Exception {
        Resource resource;
        boolean isNew = (id == null || id == 0);

        if (isNew) {
            // NOTE: If your DAO 'create' signature differs, keep your original line.
            CommunityResource r = CommunityResourceDAO.create(id, "", ResourceType.Resource, parentId, null);
            resource = Resource.getInstance(id);
            resource.populateFrom(r);
        } else {
            resource = Resource.getInstance(id);
        }

        model.addAttribute("resource", resource);
        model.addAttribute("isNew", isNew);
        model.addAttribute("coverImage", "/Cover.png");
        return EditResourcePageGenerator.generateEditPage(resource);
    }

  
}
