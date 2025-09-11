package com.lordjoe.sandhurst;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;

@Controller
public class InhabitantController {
    @GetMapping(path = "/sandhurst/addInhabitant", produces = "text/html")
    @ResponseBody
    public String addInhabitant(@RequestParam("houseId") int houseId,
                                HttpServletRequest request) {
        if (!Login.isUserAuthorized(request)) {
            return "<html><body><h3>Unauthorized</h3></body></html>";
        }

        // Build a blank inhabitant tied to the house
        Inhabitant inh = new Inhabitant(0,houseId,"","","",InhabitantType.Adult);

        // Optional default:
        // inh.setType(InhabitantType.Adult);

        // Reuse your edit page (it renders the add form when id==0)
        return EditInhabitantPageMaker.generate(inh, request);
    }

    // Renders the edit page. Includes CSRF by passing the request through to the page maker.
    @GetMapping("/sandhurst/editInhabitant")
    @ResponseBody
    public String editInhabitant(@RequestParam("inhabitantId") int id,
                                 HttpServletRequest request) {
        if (!Login.isUserAuthorized(request)) {
            return "<html><body><h3>Unauthorized</h3></body></html>";
        }

        Neighborhood neighborhood = Neighborhood.Instance;
        Inhabitant inh = neighborhood.getInhabitantById(id);
        if (inh == null) {
            return "<html><body><h3>Inhabitant not found</h3></body></html>";
        }

        return EditInhabitantPageMaker.generate(inh, request);
    }

    // Handles both Save and Cancel (you asked to treat them the same).
    // Also accepts optional image upload and "remove photo" flag.
    @PostMapping("/sandhurst/updateInhabitant")
    public RedirectView updateInhabitant(@RequestParam("id") int id,
                                         @RequestParam("name") String name,
                                         @RequestParam(value = "email", required = false) String email,
                                         @RequestParam(value = "phone", required = false) String phone,
                                         @RequestParam(value = "type", required = false) String type,
                                         @RequestParam(value = "action", required = false) String action,
                                         @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
                                         @RequestParam(value = "photo", required = false) MultipartFile photo) {

        Neighborhood n = Neighborhood.Instance;
        Inhabitant inh = n.getInhabitantById(id);
        if (inh != null) {
            // Update fields
            inh.setName(name);
            inh.setEmail(email);
            inh.setPhone(phone);

            // Map "type" string to your enum/model if present
            if (type != null) {
                try {
                    // If your project has an enum like InhabitantType, this will work.
                    // Otherwise, replace this with your own mapping / setter.
                    InhabitantType t = InhabitantType.valueOf(type);
                    inh.setType(t);
                } catch (Throwable ignored) {
                    // Ignore bad/unknown type values; keep existing type.
                }
            }

            // --- Picture handling (plug in your storage/persistence here) ---
            try {
                if (Boolean.TRUE.equals(removePhoto)) {
                    // TODO: remove existing photo from storage and from model
                    // e.g., ImageStorage.deleteInhabitantPhoto(id);
                    //       inh.setPhotoPath(null);
                } else if (photo != null && !photo.isEmpty()) {
                    // TODO: save new photo and store reference on the model
                    // e.g., String path = ImageStorage.saveInhabitantPhoto(id, photo);
                    //       inh.setPhotoPath(path);
                }
            } catch (Exception e) {
                // TODO: optionally log the exception
            }

            // Persist/update in your data layer if needed.
            // If Neighborhood keeps authoritative state, this may be enough:
            // n.updateInhabitant(inh);
        }

        int houseId = (inh != null && inh.getHouseId() != null) ? inh.getHouseId() : -1;
        String target = (houseId > 0) ? ("/sandhurst/house/" + houseId) : "/sandhurst";
        return new RedirectView(target, true);
    }

    public RedirectView createInhabitant(
                                         String name,
                                           String email,
                                       String phone,
                                         String type,
                                          String action,
                                          Boolean removePhoto,
                                         MultipartFile photo) {

        Neighborhood n = Neighborhood.Instance;

        Inhabitant inh = null;
        if(true)
            throw new UnsupportedOperationException("Fix This"); // ToDo
        if (inh != null) {
            // Update fields
            inh.setName(name);
            inh.setEmail(email);
            inh.setPhone(phone);

            // Map "type" string to your enum/model if present
            if (type != null) {
                try {
                    // If your project has an enum like InhabitantType, this will work.
                    // Otherwise, replace this with your own mapping / setter.
                    InhabitantType t = InhabitantType.valueOf(type);
                    inh.setType(t);
                } catch (Throwable ignored) {
                    // Ignore bad/unknown type values; keep existing type.
                }
            }

            // --- Picture handling (plug in your storage/persistence here) ---
            try {
                if (Boolean.TRUE.equals(removePhoto)) {
                    // TODO: remove existing photo from storage and from model
                    // e.g., ImageStorage.deleteInhabitantPhoto(id);
                    //       inh.setPhotoPath(null);
                } else if (photo != null && !photo.isEmpty()) {
                    // TODO: save new photo and store reference on the model
                    // e.g., String path = ImageStorage.saveInhabitantPhoto(id, photo);
                    //       inh.setPhotoPath(path);
                }
            } catch (Exception e) {
                // TODO: optionally log the exception
            }

            // Persist/update in your data layer if needed.
            // If Neighborhood keeps authoritative state, this may be enough:
            // n.updateInhabitant(inh);
        }

        int houseId = (inh != null && inh.getHouseId() != null) ? inh.getHouseId() : -1;
        String target = (houseId > 0) ? ("/sandhurst/house/" + houseId) : "/sandhurst";
        return new RedirectView(target, true);
    }

    // Deletes the inhabitant and redirects back to their house or /sandhurst
    @PostMapping("/sandhurst/deleteInhabitant")
    public RedirectView deleteInhabitant(@RequestParam("id") int id) {
        Neighborhood n = Neighborhood.Instance;
        Inhabitant inh = n.getInhabitantById(id);
        int houseId = (inh != null && inh.getHouseId() != null) ? inh.getHouseId() : -1;

        House house = n.getHouse(houseId);
        house.removeInhabitant(inh);
        // Remove from your persistence/store
        // Replace with your actual delete/removal call.
        n.removeInhabitant(id);
        try {
            InhabitantDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

        String target = (houseId > 0) ? ("/sandhurst/house/" + houseId) : "/sandhurst";
        return new RedirectView(target, true);
    }
}
