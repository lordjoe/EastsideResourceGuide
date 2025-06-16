package com.lordjoe.sandhurst;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.StringWriter;
import java.io.PrintWriter;

@Controller
public class SandhurstMapController {

    @GetMapping("/sandhurst")
    @ResponseBody
    public String showMap() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        String mapApiKey = MapsKeyFetcher.getMapsApiKey(); // ensure this is available
        MapPageGenerator.writeMapPage(out, mapApiKey);
        return writer.toString();
    }
}

