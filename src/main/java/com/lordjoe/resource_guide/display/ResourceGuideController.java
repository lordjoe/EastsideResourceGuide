package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.sandhurst.Login;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ResourceGuideController {

    @GetMapping("/")
    public void redirectToMain(HttpServletResponse response) throws IOException {
        response.sendRedirect("/main");
    }

    @GetMapping("/main")
    @ResponseBody
    public String mainContent(HttpServletRequest request) {
        Guide guide = Guide.Instance;
        List<Catagory> categories = guide.getCatagories();
        return generateHomePage(categories, request);
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Invalidate the token cookie by clearing it
        Cookie cookie = new Cookie("EASTSIDE_LOGIN_TOKEN", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete it
        response.addCookie(cookie);

        // Optionally invalidate session if you're using sessions
        // request.getSession().invalidate();

        return "redirect:/login?logout=true";
    }


    public String generateHomePage(List<Catagory> categories, HttpServletRequest request) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("  <link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">\n");
        html.append("  <title>Eastside Resource Guide</title>\n");
        addCSS(html);
        html.append("</head><body>");

        if (Login.isUserAuthorized(request)) {
            html.append("<div style=\"text-align: right; margin-bottom: 10px;\">\n");
            html.append("<form action=\"/logout\" method=\"post\" style=\"display:inline;\">\n");
            html.append("<button type=\"submit\" class=\"top-button\">Logout</button>\n");
            html.append("</form></div>\n");
        }

        html.append("<h1>Eastside Resource Guide</h1>");

        html.append("<div class=\"top-categories\">");
        for (Catagory cat : categories) {
            String name = cat.getName();
            if (name.equalsIgnoreCase("Introduction") || name.equalsIgnoreCase("Unclassified")) {
                html.append("<form action=\"/category\" method=\"get\" style=\"display:inline-block; margin: 0 10px;\">");
                html.append("<input type=\"hidden\" name=\"category\" value=\"" + escapeHtml(name) + "\"/>");
                html.append("<button class=\"button\" type=\"submit\">" + escapeHtml(name) + "</button>");
                html.append("</form>");
            }
        }
        html.append("</div>");

        html.append("<div class=\"grid-container\">");
        for (Catagory cat : categories) {
            String name = cat.getName();
            if (name.equalsIgnoreCase("Introduction") || name.equalsIgnoreCase("Unclassified")) continue;

            Color c = ColorMap.getColor(name);
            String color = colorToHex(c);

            html.append("<form action=\"/category\" method=\"get\">");
            html.append("<input type=\"hidden\" name=\"category\" value=\"" + escapeHtml(name) + "\"/>");
            html.append("<button class=\"button\" style=\"background-color: " + color + ";\" type=\"submit\">");
            html.append(escapeHtml(name));
            html.append("</button>");
            html.append("</form>");
        }
        html.append("</div></body></html>");

        return html.toString();
    }

    @GetMapping("/login")
    @ResponseBody
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout
    ) {
        boolean showError = (error != null);
        boolean showLogout = (logout != null);
        return LoginPageGenerator.generateLoginPage(showError, showLogout);
    }


    private void addCSS(StringBuilder html) {
        html.append("<style>");
        html.append("body { background-image: url('/Cover.png'); background-size: cover; background-position: center; font-family: Arial, sans-serif; margin: 0; padding: 20px; } ");
        html.append("h1 { color: black; text-align: center; margin-top: 20px; font-size: 64px; } ");
        html.append(".top-categories { text-align: center; margin-top: 30px; } ");
        html.append(".top-button { font-size: 20px; padding: 10px 20px; border-radius: 8px; border: none; background-color: #cccccc; margin: 10px; cursor: pointer; text-decoration: none; display: inline-block; } ");
        html.append(".grid-container { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-top: 50px; } ");
        html.append("@media screen and (max-width: 900px) { .grid-container { grid-template-columns: repeat(2, 1fr); } } ");
        html.append(".button { width: 100%; padding: 20px; font-size: 20px; border: none; cursor: pointer; border-radius: 8px; color: black; } ");
        html.append("</style>");
    }

    private String colorToHex(java.awt.Color color) {
        if (color == null) return "#CCCCCC";
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @GetMapping("/category")
    @ResponseBody
    public String showCategory(@RequestParam("category") String categoryName) {
        Guide guide = Guide.Instance;
        Catagory cat = guide.getCatagoryByName(categoryName);
        if (cat == null) {
            return "<html><body><h1>Category not found</h1><a href='/'>Back to Home</a></body></html>";
        }
        return CategoryPageGenerator.generateCategoryPage(cat);
    }

    @GetMapping("/search")
    public String searchResources(@RequestParam("query") String query, Model model) throws SQLException {
        List<CommunityResource> resources = null; // Placeholder for DAO
        List<CommunityResource> filtered = resources.stream()
                .filter(r -> (r.getName() != null && r.getName().toLowerCase().contains(query.toLowerCase())) ||
                        (r.getDescription() != null && r.getDescription().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());

        model.addAttribute("query", query);
        model.addAttribute("results", filtered);
        return "search";
    }
}
