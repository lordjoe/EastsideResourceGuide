package com.lordjoe.sandhurst;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HouseController {

    @GetMapping("/sandhurst/house/{id}")
    @ResponseBody
    public String showHouse(@PathVariable int id) {
        House house = Neighborhood.Instance.getHouse(id);
        if (house == null) {
            return "<html><body><h1>House not found</h1><a href='/sandhurst'>Back to Sandhurst</a></body></html>";
        }
        return HousePageMaker.generate(house,null);
    }
}
