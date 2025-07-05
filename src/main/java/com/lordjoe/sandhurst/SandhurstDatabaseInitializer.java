package com.lordjoe.sandhurst;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class SandhurstDatabaseInitializer {

    @PostConstruct
    public void init() throws Exception {
        Database.clearDatabase();
        Database.createDatabase();
        Database.loadHousesFromTSV("kirkland_houses.tsv");
        Database.loadInhabitantsFromTSV("sandhurst_people.csv");
        Neighborhood.loadInHabitants();
        File dir = new File("Sandhurst");
        File parcel = new File(dir, "SandhurstParcelReport.tsv.csv");
 //       Database.loadParcelsFromTSV(parcel);
        List<House> houses = Neighborhood.Instance.getHouses();
        for (House house : houses) {
            for (Inhabitant inhabitant : house.getInhabitants()) {
                String namex = inhabitant.getName().replace(" ","-").replace(",","");
                System.out.println("https://www.fastpeoplesearch.com/name/"  + namex + "_kirkland-wa");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new SandhurstDatabaseInitializer().init();
    }
}

