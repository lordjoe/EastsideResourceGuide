package com.lordjoe.sandhurst;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * com.lordjoe.sandhurst.Nirghborhood
 * User: Steve
 * Date: 6/16/25
 */
public class Neighborhood {
    public static final Neighborhood Instance = new Neighborhood();

    private final List<House> houses;
    private final Map<Integer, House> idToHouse = new HashMap<Integer, House>();

    Neighborhood() {
        houses = HouseLoader.loadAllHouses();
        for (House house : houses) {
            idToHouse.put(house.getId(), house);
        }
    }

    public List<House> getHouses() {
        return houses;
    }

    public House getHouse(int id) {
        return idToHouse.get(id);
    }
}
