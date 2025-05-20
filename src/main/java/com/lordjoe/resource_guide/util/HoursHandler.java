package com.lordjoe.resource_guide.util;

import java.util.regex.Pattern;

public class HoursHandler {

    private static final Pattern HOURS_PATTERN = Pattern.compile(
            "(?i)\\b(?:mon(?:day)?|tue(?:sday)?|wed(?:nesday)?|thu(?:rsday)?|fri(?:day)?|sat(?:urday)?|sun(?:day)?)s?\\b[\\s,:-]*\\d{1,2}(?::\\d{2})?\\s*(AM|PM)?\\s*(?:-|to|–)\\s*\\d{1,2}(?::\\d{2})?\\s*(AM|PM)?"
    );

    private static final Pattern TIME_ONLY_PATTERN = Pattern.compile(
            "(?i)\\b\\d{1,2}(?::\\d{2})?\\s*(AM|PM)?\\s*(–|-|to)\\s*\\d{1,2}(?::\\d{2})?\\s*(AM|PM)?\\b"
    );

    private static final Pattern HOUR_KEYWORDS_PATTERN = Pattern.compile(
            "(?i)(daily|hours|office hours|open|certified).*\\d{1,2}(:\\d{2})?\\s*(AM|PM)?.*"
    );

    private static final Pattern TWENTY_FOUR_HOUR_PATTERN = Pattern.compile(
            "(?i).*24[- ]?hour.*"
    );

    public static boolean isHours(String text) {
        return HOURS_PATTERN.matcher(text).find()
                || TIME_ONLY_PATTERN.matcher(text).find()
                || HOUR_KEYWORDS_PATTERN.matcher(text).find()
                || TWENTY_FOUR_HOUR_PATTERN.matcher(text).find();
    }
}
