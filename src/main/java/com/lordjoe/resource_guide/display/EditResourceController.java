package com.lordjoe.resource_guide.display;

/**
 * com.lordjoe.resource_guide.display.EditResourdeController
 * User: Steve
 * Date: 5/20/25
 */


import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class EditResourceController {

    @GetMapping("/edit-resource")
    @ResponseBody
    public String editResourcePage(@RequestParam("id") int id) {
        CommunityResource resource = Guide.Instance.getResourceById(id);
        if (resource == null) {
            return "<html><body><h1>Resource not found</h1><a href='/'>Home</a></body></html>";
        }
        return EditResourcePageGenerator.generateEditPage(resource);
    }

    @PostMapping("/update-resource")
    @ResponseBody
    public String updateResource(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("website") String website,
            @RequestParam("address") String address,
            @RequestParam("hours") String hours,
            @RequestParam("notes") String notes
    ) {
        CommunityResource resource = Guide.Instance.getResourceById(id);
        if (resource == null) {
            return "<html><body><h1>Resource not found</h1><a href='/'>Home</a></body></html>";
        }

        resource.setName(name);
        resource.setDescription(description);
        resource.setPhone(phone);
        resource.setEmail(email);
        resource.setWebsite(website);
        resource.setAddress(address);
        resource.setHours(hours);
        resource.setNotes(notes);

        CommunityResourceDAO.update(resource);

        return "<html><body><h1>Resource updated</h1><a href='/'>Back to Home</a></body></html>";
    }
}
