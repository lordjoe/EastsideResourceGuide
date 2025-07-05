package com.lordjoe.sandhurst;

import com.lordjoe.resource_guide.util.NameSimilarityUtil;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.*;

public class House {
    public final int id;
    public final double latitude;
    public final double longitude;
    public final String address;
    public final List<Inhabitant> inhabitants = new ArrayList<Inhabitant>() ;
    public final List<ImageAsset> images = new ArrayList<ImageAsset>() ;

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public House(int id, double latitude, double longitude, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public List<Inhabitant> getInhabitants() {
        return inhabitants;
    }

    public Inhabitant findInhabitant(String name) {
         Map<String,Inhabitant> byName = new HashMap<String,Inhabitant>() ;
         List<String> names = new ArrayList<>();
        for (Inhabitant inhabitant : inhabitants) {
            byName.put(inhabitant.getName(),inhabitant);
            names.add(name);
        }
        if (byName.containsKey(name)) {
            return byName.get(name);
        }
        NameSimilarityUtil.MatchResult bestMatch = NameSimilarityUtil.findBestMatch(name, names);
        if(bestMatch.isConfidentMatch(0.6)) {
            return byName.get(RequestMatcher.MatchResult.match().toString());
        }
        return null;
    }

    public void addInhabitant(Inhabitant inhabitant) {
        inhabitants.add(inhabitant);
    }

    public List<ImageAsset> getImages() {
        return new ArrayList<>(images);
    }

    public void addImage(ImageAsset image) {
        images.add(image);
    }

    public void removeImage(ImageAsset image) {
        images.remove(image);
    }
}
