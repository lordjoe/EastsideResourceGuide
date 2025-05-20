package com.lordjoe.resource_guide.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.lordjoe.resource_guide.util.HoursParser
 * User: Steve
 * Date: 5/2/25
 *
 * Utility class to detect and parse text lines that specify hours of operation.
 */
public class HoursParser {

    // Example patterns: "Mon-Fri 9AM-5PM", "Monday to Friday, 8:00AM - 4:30PM"
    private static final Pattern HOURS_PATTERN = Pattern.compile(
            "(?i)(Mon(day)?|Tue(sday)?|Wed(nesday)?|Thu(rsday)?|Fri(day)?|Sat(urday)?|Sun(day)?)(\s*[-to]*\s*(Mon(day)?|Tue(sday)?|Wed(nesday)?|Thu(rsday)?|Fri(day)?|Sat(urday)?|Sun(day)?))?[,\s]*\\d{1,2}(:\\d{2})?(AM|PM|am|pm)?\s*[-to]*\s*\\d{1,2}(:\\d{2})?(AM|PM|am|pm)?"
    );

    /**
     * Check if a line looks like it contains hours of operation.
     *
     * @param line The input text line
     * @return true if likely an hours line
     */
    public static boolean isHoursLine(String line) {
        if (line == null || line.isBlank())
            return false;
        Matcher matcher = HOURS_PATTERN.matcher(line.trim());
        return matcher.find();
    }

    /**
     * Extract the hours information from the line if present.
     *
     * @param line The input text line
     * @return The matched hours text, or null if not found
     */
    public static String extractHours(String line) {
        if (line == null || line.isBlank())
            return null;
        Matcher matcher = HOURS_PATTERN.matcher(line.trim());
        if (matcher.find()) {
            return matcher.group().trim();
        }
        return null;
    }
}
