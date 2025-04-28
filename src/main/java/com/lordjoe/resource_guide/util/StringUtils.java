package com.lordjoe.resource_guide.util;

/**
 * com.lordjoe.resource_guide.util.StringUtils
 * User: Steve
 * Date: 4/27/25
 */
public class StringUtils {

    public static String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    public static String FileToCatagory(String filename) {

        filename = filename.replace("Copy of ","" );
        filename = filename.replace("COMPLETED","" );
        filename = filename.replace("-","" );
        filename.trim();
        
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex);
        }
        return filename;
    }
}
