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

    /*
        test two strings have equivalent text - can handle nulls
     */
    public static boolean equivalentText(String s1, String s2) {
        if( s1 == null && s2 == null )
            return true;
        if( s1 == null && s2 != null )
            return false;
        String s =  textOnly(s1);
        String sx2 = textOnly(s2);
        return s.equals(sx2);
    }

    /*
       convert a string to the printable parts = useful for
       comparisons
     */
    public static String textOnly(String s) {
        if(s == null) return null;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(Character.isLetterOrDigit(c))
                sb.append(c);
        }
        return sb.toString();
    }
}
