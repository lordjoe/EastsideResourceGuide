package com.lordjoe.sandhurst;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

@Controller
public class SandhurstMapController {

     @GetMapping(path = "/sandhurst", produces = "text/html")
    @ResponseBody
    public String showMap() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        String mapApiKey = MapsKeyFetcher.getMapsApiKey(); // ensure this is available
        MapPageGenerator.writeMapPage(out, mapApiKey);
        return writer.toString();
    }

    @GetMapping("/sandhurst/editInhabitant")
    @ResponseBody
    public String editInhabitant(@RequestParam("inhabitantId") int id ) {
        if (!Login.isUserAuthorized(null)) {
            return "<html><body><h3>Unauthorized</h3></body></html>";
        }

        Neighborhood neighborhood = Neighborhood.Instance ;
        Inhabitant inh = neighborhood.getInhabitantById(id);
        if (inh == null) {
            return "<html><body><h3>Inhabitant not found</h3></body></html>";
        }

        return EditInhabitantPageMaker.generate(inh);
    }

 
}

