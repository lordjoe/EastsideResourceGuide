package com.lordjoe.sandhurst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
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

    public static void main(String[] args) throws Exception {
        Database.clearDatabase();
        Database.createDatabase();
        Database.loadHousesFromTSV("kirkland_houses.tsv"); // Ensure path is correct
        org.springframework.boot.SpringApplication.run(MapController.class, args);
    }
}


