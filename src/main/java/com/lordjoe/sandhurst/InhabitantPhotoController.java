package com.lordjoe.sandhurst;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class InhabitantPhotoController {

    @GetMapping(value = "/sandhurst/inhabitant/photo")
    public void photo(@RequestParam("id") int id, HttpServletResponse response) throws IOException {
        Inhabitant inhabitant = Neighborhood.Instance.getInhabitantById(id);
        if (inhabitant == null || inhabitant.getImages().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Redirect to the first image URL
        response.sendRedirect(inhabitant.getImages().get(0).getImageUrl());
    }
}
