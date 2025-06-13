package com.lordjoe.sandhurst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * com.lordjoe.sandhurst.MapController
 * User: Steve
 * Date: 6/12/25
 */

@Controller
public class MapController {

    @Autowired
    private HouseRepository houseRepository;

    @GetMapping("/sandhurst")
    @ResponseBody
    public String showMap() {
        List<House> houses = houseRepository.findAll();
        return MapPageGenerator.generate(houses);
    }
}

 
