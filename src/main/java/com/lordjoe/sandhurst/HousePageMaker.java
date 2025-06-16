package com.lordjoe.sandhurst;


public class HousePageMaker {

    public static String generate(House house) throws Exception {
        String apiKey = MapsKeyFetcher.getMapsApiKey();

        return """
                <html>
                <head>
                    <title>%s</title>
                </head>
                <body>
                    <h1>%s</h1>
                    <p><a href="/sandhurst">Sandhurst</a></p>

                    <img src="/images/%s.jpg" alt="House photo" width="400"/><br/><br/>

                    <iframe
                      width="600" height="400"
                      src="https://www.google.com/maps/embed/v1/place?key=%s&q=%f,%f"
                      allowfullscreen>
                    </iframe>
                </body>
                </html>
                """.formatted(
                house.getAddress(),
                house.getAddress(),
                house.getId(),  // Assuming image is named like `12.jpg` in /images/
                apiKey,
                house.getLatitude(),
                house.getLongitude()
        );
    }

}
