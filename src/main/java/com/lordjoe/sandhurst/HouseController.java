package com.lordjoe.sandhurst;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HouseController {

    @GetMapping("/sandhurst/house/{id}")
    @ResponseBody
    public String showHouse(@PathVariable int id, HttpServletRequest request) {
        House house = Neighborhood.Instance.getHouse(id);
        if (house == null) {
            return "<html><body><h1>House not found</h1><a href='/sandhurst'>Back to Sandhurst</a></body></html>";
        }
        return HousePageMaker.generate(house,request);
    }
}
