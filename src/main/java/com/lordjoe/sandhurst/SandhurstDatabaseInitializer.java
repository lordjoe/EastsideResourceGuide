package com.lordjoe.sandhurst;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SandhurstDatabaseInitializer {

    @PostConstruct
    public void init() throws Exception {
        Database.clearDatabase();
        Database.createDatabase();
        Database.loadHousesFromTSV("kirkland_houses.tsv");
    }
}

