
package com.lordjoe.sandhurst;

public class HouseImageResolver {

    public static String getImageUrl(House house) {
        // Example: look for images under /images/houses/10502_NE_44th_St.jpg
        String sanitized = house.getAddress().replaceAll("[^a-zA-Z0-9]", "_");
        String path = "/images/houses/" + sanitized + ".jpg";

        // You could add logic to check file existence if needed
        return path;
    }
}