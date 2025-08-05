package com.lordjoe.resource_guide.dao;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.model.CommunityResource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/")
public class ResourceUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUpdateController.class);

    @PostMapping("update-resource")
    public RedirectView updateResource(
            @RequestParam("id") int id,
            @RequestParam(value = "parentId", required = false) Integer parentId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "hours", required = false) String hours,
            @RequestParam(value = "notes", required = false) String notes,
            HttpServletRequest request) {  // <-- FIXED THIS LINE

        logger.info("Updating resource with id: {}, parentId: {}, name: {}", id, parentId, name);

        CommunityResource resource = Guide.Instance.getResourceById(id);

        if (resource == null) {
            resource = new CommunityResource();
            resource.setId(0);
        }

        if (parentId != null) {
            resource.setParentId(parentId);
        }
        resource.setName(name);
        resource.setDescription(description);
        resource.setPhone(phone);
        resource.setEmail(email);
        resource.setWebsite(website);
        resource.setAddress(address);
        resource.setHours(hours);
        resource.setNotes(notes);

        if (id == 0) {
            try {
                CommunityResourceDAO.insert(resource);
                Guide.Instance.addResource(resource); // Add new resource to in-memory guide
            } catch (Exception e) {
                logger.error("Failed to insert new resource", e);
                throw new RuntimeException("Failed to insert new resource", e);
            }
        } else {
            CommunityResourceDAO.update(resource);
            Guide.Instance.addResource(resource); // Refresh updated copy
        }

        Integer redirectId = resource.getParentId();
        if (redirectId == null) {
            return new RedirectView("/main"); // Redirect to a default page if parentId is null
        } else {
            Catagory cat = Guide.Instance.getCatagoryById(redirectId);
            if (cat != null) {
                 cat = cat.getCatagory();
                return new RedirectView("/category?category=" + URLEncoder.encode(cat.getName(), StandardCharsets.UTF_8));
            } else
                return new RedirectView("/main");
        }
    }

    @PostMapping("delete-resource")
    public RedirectView deleteResource(@RequestParam("id") int id) {
        CommunityResource resource = Guide.Instance.getResourceById(id);
        if (resource != null) {
            CommunityResourceDAO.delete(id);
            Guide.Instance.removeResource(resource);
            return new RedirectView("/main/category?id=" + resource.getParentId());
        } else {
            return new RedirectView("/main");
        }
    }
}
