package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Guide;
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
        CommunityResource resource;
        boolean isNew = (id == null || id == 0);

        if (isNew) {
            resource = new CommunityResource();
            resource.setId(0);
            resource.setParentId(parentId);
            resource.setType(ResourceType.Resource);
        } else {
            resource = Guide.Instance.getResourceById(id);
            resource.setParentId(parentId);
        }

        model.addAttribute("resource", resource);
        model.addAttribute("isNew", isNew);
        model.addAttribute("coverImage", "/Cover.png");
        return EditResourcePageGenerator.generateEditPage(resource);
    }
}
