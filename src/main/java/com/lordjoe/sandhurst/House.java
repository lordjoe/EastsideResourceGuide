package com.lordjoe.sandhurst;

public class House {
    public final int id;
    public final double latitude;
    public final double longitude;
    public final String address;

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


}
