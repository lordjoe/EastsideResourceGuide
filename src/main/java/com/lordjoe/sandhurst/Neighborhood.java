package com.lordjoe.sandhurst;


import com.lordjoe.resource_guide.util.DatabaseConnection;
import com.lordjoe.resource_guide.util.NameSimilarityUtil;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private final Map<Integer, Inhabitant> idToInhabitant = new HashMap<Integer, Inhabitant>();
    private final Map<String, House> addressToHouse = new HashMap<String, House>();



    Neighborhood() {
        houses = HouseLoader.loadAllHouses();
        for (House house : houses) {
            idToHouse.put(house.getId(), house);
            addressToHouse.put(house.getAddress().toUpperCase() , house);
        }
    }

    public static void loadInHabitants() {
             try (Connection conn = DatabaseConnection.getConnection()) {
                InhabitantDAO ix = new InhabitantDAO(conn);
                ix.loadAllInhabitants();
            }
            catch (SQLException ex)   {
                ex.printStackTrace();
            }
        for (House house : Instance.getHouses()) {
            for (Inhabitant inhabitant : house.getInhabitants()) {
                Instance.idToInhabitant.put(inhabitant.getId(), inhabitant);
            }
        }
        }


    public List<House> getHouses() {
        return houses;
    }

    public House getHouse(int id) {
        return idToHouse.get(id);
    }

    public House getHouse(String address) {
        return addressToHouse.get(address.toUpperCase());
    }

    public House findHouse(String address) {
        House ret = getHouse(address);
        if (ret != null) {
            return ret;
        }
        address = address.toUpperCase();
       List<String> addresses = new ArrayList<String>(addressToHouse.keySet());
        NameSimilarityUtil.MatchResult bestMatch = NameSimilarityUtil.findBestMatch(address, addresses);
        if(bestMatch.isConfidentMatch(0.6)) {
           return getHouse(RequestMatcher.MatchResult.match().toString());
        }
        return null;
    }




    public Inhabitant getInhabitantById(int id) {
        return idToInhabitant.get(id);
    }
}
