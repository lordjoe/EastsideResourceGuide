package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        }

        model.addAttribute("resource", resource);
        model.addAttribute("isNew", isNew);
        model.addAttribute("coverImage", "/Cover.png");
        return EditResourcePageGenerator.generateEditPage(resource );
    }

    @PostMapping("/update-resource")
    public String updateResource(@RequestParam("id") int id,
                                 @RequestParam("name") String name,
                                 @RequestParam("parentId") int parentId,
                                 @RequestParam(value = "description", required = false) String description,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "email", required = false) String email,
                                 @RequestParam(value = "website", required = false) String website,
                                 @RequestParam(value = "address", required = false) String address,
                                 @RequestParam(value = "hours", required = false) String hours,
                                 @RequestParam(value = "notes", required = false) String notes) throws Exception {

        CommunityResource resource = new CommunityResource(id, name, ResourceType.Resource, parentId);
        resource.setDescription(description);
        resource.setPhone(phone);
        resource.setEmail(email);
        resource.setWebsite(website);
        resource.setAddress(address);
        resource.setHours(hours);
        resource.setNotes(notes);

        if (id == 0) {
            CommunityResourceDAO.insert(resource);
        } else {
            CommunityResourceDAO.update(resource);
        }

        String catName = Guide.Instance.getTopLevelCategory(parentId).getName();
        return "redirect:/category?category=" + catName;
    }

    @PostMapping("/delete-resource")
    public String deleteResource(@RequestParam("id") int id) throws Exception {
        CommunityResource resource = Guide.Instance.getResourceById(id);
        if (resource != null) {
            CommunityResourceDAO.delete(id);
            String catName = Guide.Instance.getTopLevelCategory(resource.getParentId()).getName();
            return "redirect:/category?category=" + catName;
        }
        return "redirect:/";
    }
}
