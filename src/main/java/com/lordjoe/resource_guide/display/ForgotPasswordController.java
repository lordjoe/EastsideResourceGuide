package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.model.AppUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class ForgotPasswordController {

    @PostMapping("/forgot-password")
    public RedirectView handleForgotPassword(@RequestParam("email") String email) {
        AppUser user = Guide.Instance.getUser(email);
        String message;

        if (user != null) {
            user.sendPasswordEmail();
            message = "An email with your password has been sent.";
        } else {
            message = "There is no such user on the system.";
        }

        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        return new RedirectView("/login?msg=" + encodedMessage);
    }
}
