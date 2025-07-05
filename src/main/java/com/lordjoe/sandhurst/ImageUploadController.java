package com.lordjoe.sandhurst;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageUploadController {

    @PostMapping("/sandhurst/uploadImage")
    public String uploadImage(
            @RequestParam("inhabitantId") int inhabitantId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        try {
            // Upload to Firebase and get URL
            String url = FirebaseUploader.uploadFile(file); // assumes this method exists

            // Create and associate image asset
            ImageAsset asset = new ImageAsset(inhabitantId, ImageAssetType.inhabitant, url);
            ImageAssetDAO.insert(asset);
            Neighborhood.Instance.getInhabitantById(inhabitantId).getImages().add(asset);

            // Redirect back to edit page
            return "redirect:/sandhurst/editInhabitant?inhabitantId=" + inhabitantId;

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error?message=upload_failed";
        }
    }
}
