package com.lordjoe.sandhurst;

public class HousePageMaker {

    public static String generate(House house)  {
        String apiKey = MapsKeyFetcher.getMapsApiKey();

        return """
                <html>
                <head>
                    <title>%s</title>
                </head>
                <body>
                    <h1>%s</h1>
                    <p><a href="/sandhurst">Sandhurst</a></p>

                    <img src="https://maps.googleapis.com/maps/api/streetview?size=600x300&location=%f,%f&key=%s"
                         alt="Street View" width="600" height="300"/><br/><br/>


                </body>
                </html>
                """.formatted(
                house.getAddress(),
                house.getAddress(),
                house.getLatitude(),
                house.getLongitude(),
                apiKey,
                apiKey,
                house.getLatitude(),
                house.getLongitude()
        );
    }
}

