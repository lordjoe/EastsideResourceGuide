package com.lordjoe.resource_guide.display;

import com.lordjoe.resource_guide.Catagory;
import com.lordjoe.resource_guide.Guide;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CategoryPageController {

    @GetMapping(path = "/main/category", produces = "text/html")
    @ResponseBody
    public String showCategoryById(@RequestParam("id") int id) {
        Catagory cat = Guide.Instance.getCatagoryById(id);
        if (cat == null) {
            // simple not-found page; no DB reads
            return "<html><body><h3>Category not found: " + id + "</h3></body></html>";
        }
        // Your existing renderer (adjust the class/method if named differently)
        return CategoryPageGenerator.generateCategoryPage(cat);
    }
}
