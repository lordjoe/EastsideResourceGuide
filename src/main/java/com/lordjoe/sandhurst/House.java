package com.lordjoe.sandhurst;

import java.util.ArrayList;
import java.util.List;

public class House {
    public final int id;
    public final double latitude;
    public final double longitude;
    public final String address;
    public final List<Inhabitant> inhabitants = new ArrayList<Inhabitant>() ;

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

    public void addInhabitant(Inhabitant inhabitant) {
        inhabitants.add(inhabitant);
    }
}
