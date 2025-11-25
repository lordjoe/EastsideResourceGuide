package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.model.ResourceDraftService;
import com.lordjoe.resource_guide.model.ResourceDraftService.DraftInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class CancelNewResourceController {

    @Autowired
    private ResourceDraftService drafts;

    @PostMapping("/resources/new/cancel")
    public String cancelNew(@RequestParam("draftId") String draftId) {
        DraftInfo info = drafts.discardDraft(draftId, Guide.Instance);
        // If we have a persisted row, perform a cascade delete via DAO static API
        if (info != null && info.getPersistedId() != null && info.getPersistedId() > 0) {
            CommunityResourceDAO.deleteResourceAndDependents(info.getPersistedId());
        }
        int parentId = (info != null) ? info.parentId : 0;
        Catagory catagoryById = Guide.Instance.getCatagoryById(parentId);
        Catagory catagory = catagoryById.getCatagory();
        return "redirect:/main/category?id=" + catagory.getId();
    }
}
