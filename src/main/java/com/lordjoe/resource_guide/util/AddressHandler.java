package com.lordjoe.resource_guide.util;

import java.util.regex.Pattern;

public class AddressHandler {

    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "^\\s*(\\d+\\s+.*\\b(?:St|Street|Ave|Avenue|Blvd|Boulevard|Road|Rd|Dr|Drive|Ct|Court|Pl|Place|Ln|Lane|Way|Pkwy|Parkway|Terrace|Loop|Trail|Cir|Circle|Rte|Route|Hwy|Highway)\\b.*|" +
                    "P\\.?O\\.? Box \\d+)",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean isAddress(String text) {
        return ADDRESS_PATTERN.matcher(text.trim()).find();
    }
}
