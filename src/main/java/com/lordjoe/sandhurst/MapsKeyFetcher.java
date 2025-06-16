package com.lordjoe.sandhurst;

import com.lordjoe.utilities.Encrypt;

public class MapsKeyFetcher {
    public static final String ENCRYPTED_KEY = "oyVsxiXkqYxEc3oEJybWroGHDz4IBNTLoql3HcaV/eyJdKrSKU82raK8tx+LnqEYory3H4ueoRiivLcfi56hGKK8tx+LnqEYory3H4ueoRiivLcfi56hGKK8tx+LnqEYory3H4ueoRiivLcfi56hGKK8tx+LnqEY";

    public static String getMapsApiKey()   {
         return Encrypt.decryptString(ENCRYPTED_KEY);
     }

    public static void main(String[] args) {
        System.out.println(getMapsApiKey());
    }
}
