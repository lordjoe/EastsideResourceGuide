package com.lordjoe.resource_guide.display;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * com.lordjoe.resource_guide.display.ColorMap
 * User: Steve
 * Date: 4/28/25
 */
public class ColorMap {
    public static final ColorMap[] EMPTY_ARRAY = {};

    public static final Map<String, Color> CategoryColors = new HashMap<String, Color>();
    static {

    }

    public static Color getColor(String name) {
        guaranteeMap();
        return CategoryColors.get(name);
    }

    public static String getHexColor(String name) {
         return colorToHex(getColor(name));
    }
    public static String colorToHex(java.awt.Color color) {
        if (color == null) return "#CCCCCC";  // fallback light gray if null
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static void addColor(String name, Color color) {
        CategoryColors.put(name.toUpperCase(), color);
        CategoryColors.put(name, color);
    }
    private static void guaranteeMap() {
        if(CategoryColors.size() > 0)
            return;
        addColor("Adult Day Programs", new Color(243, 156, 18));
        addColor("Emergency Assistance Programs", new Color(146, 43, 33));
        addColor("Emergency Preparedness", new Color(123, 36, 28));
        addColor("Food & Meal Programs", new Color(252, 243, 207));
        addColor("End of Life Resources", new Color(123, 36, 28));
        addColor("Government Programs", new Color(69, 179, 157));
        addColor("Govt Programs", new Color(69, 179, 157));
        addColor("Health Care for Specific Needs", new Color(88, 214, 141));
        addColor("Health Care", new Color(88, 214, 141));
        addColor("Health Care General", new Color(88, 214, 141));
        addColor("Housing", new Color(84, 153, 199));
        addColor("In Home Services", new Color(20, 143, 119 ));
        addColor("Information and Referral Services", new Color(21, 67, 96));
        addColor("Info & Referral Svcs", new Color(21, 67, 96));
        addColor("Info & Referral Services", new Color(21, 67, 96));
        addColor("Legal Services", new Color(212, 172, 13));
        addColor("Legal Resources", new Color(212, 172, 13));
        addColor("Mental Health", new Color(165, 105, 189));
        addColor("Personal Finances & Taxes", new Color(25, 111, 61 ));
        addColor("Pets & Veteranary Services", new Color(155, 89, 182));
        addColor("Pets & Vet Services", new Color(155, 89, 182));
        addColor("Recreation & Education", new Color(31, 97, 141));
        addColor("Transportation", new Color(142, 68, 173));
        addColor("Veterans Services & Information", new Color(31, 97, 141));
        addColor("Veterans Services & Info", new Color(31, 97, 141));
        addColor("Introduction", new Color(255,255,255));
        addColor("Unclassifiec", new Color(255,255,255));

    }
}
