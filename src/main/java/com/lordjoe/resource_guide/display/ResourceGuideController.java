package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.model.CommunityResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ResourceGuideController {


    @GetMapping("/")
    @ResponseBody
    public String homePage() {
        Guide guide = Guide.Instance;
        List<Catagory> categories = guide.getCatagories();

        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("  <link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">\n");
        html.append("  <title>Eastside Resource Guide</title>\n");
        addCSS(html);  // now includes media query
        html.append("</head><body>");

        html.append("<h1>Eastside Resource Guide</h1>");

        // Top: Show "Introduction" and "Unclassified"
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

        // Grid layout for other categories
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
        html.append("</div>"); // end grid
        html.append("</body></html>");

        return html.toString();
    }

    private void addCSS(StringBuilder html) {
        html.append("<style>");
        html.append("body { ");
        html.append("    background-image: url('/Cover.png'); ");
        html.append("    background-size: cover; ");
        html.append("    background-position: center; ");
        html.append("    font-family: Arial, sans-serif; ");
        html.append("    margin: 0; ");
        html.append("    padding: 20px; ");
        html.append("} ");

        html.append("h1 { ");
        html.append("    color: black; ");
        html.append("    text-align: center; ");
        html.append("    margin-top: 20px; ");
        html.append("    font-size: 64px; ");
        html.append("} ");

        html.append(".top-categories { ");
        html.append("    text-align: center; ");
        html.append("    margin-top: 30px; ");
        html.append("} ");
        html.append(".top-button { ");
        html.append("    font-size: 20px; ");
        html.append("    padding: 10px 20px; ");
        html.append("    border-radius: 8px; ");
        html.append("    border: none; ");
        html.append("    background-color: #cccccc; ");
        html.append("    margin: 10px; ");
        html.append("    cursor: pointer; ");
        html.append("} ");

        html.append(".grid-container { ");
        html.append("    display: grid; ");
        html.append("    grid-template-columns: repeat(4, 1fr); ");
        html.append("    gap: 20px; ");
        html.append("    margin-top: 50px; ");
        html.append("} ");

        html.append("@media screen and (max-width: 900px) { ");
        html.append("  .grid-container { ");
        html.append("    grid-template-columns: repeat(2, 1fr); ");
        html.append("  } ");
        html.append("} ");

        html.append(".button { ");
        html.append("    width: 100%; ");
        html.append("    padding: 20px; ");
        html.append("    font-size: 20px; ");
        html.append("    border: none; ");
        html.append("    cursor: pointer; ");
        html.append("    border-radius: 8px; ");
        html.append("    color: black; ");
        html.append("} ");
        html.append("</style>");
    }

    private String colorToHex(java.awt.Color color) {
        if (color == null) return "#CCCCCC"; // fallback light gray
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
        List<CommunityResource> resources = null; //CommunityResourceDAO.loadAllResources();

        List<CommunityResource> filtered = resources.stream()
                .filter(r -> (r.getName() != null && r.getName().toLowerCase().contains(query.toLowerCase())) ||
                        (r.getDescription() != null && r.getDescription().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());

        model.addAttribute("query", query);
        model.addAttribute("results", filtered);
        return "search"; // templates/search.html
    }
}
