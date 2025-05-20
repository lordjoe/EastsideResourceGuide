package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.AddressHandler;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

/**
 * com.lordjoe.resource_guide.TestAddresses
 * User: Steve
 * Date: 5/9/25
 */
public class TestAddresses {
    public static final TestAddresses[] EMPTY_ARRAY = {};
    public static void main(String[] args) throws Exception {
        File dir = new File("test");
        StringBuilder sb = new StringBuilder();

        if (dir.exists() && dir.isDirectory()) {
            File f = new File(dir, "addresses.txt");
            LineNumberReader lnr = new LineNumberReader(new FileReader(f));
            String text = null;
            while ((text = lnr.readLine()) != null) {
                if (!AddressHandler.isAddress(text))
                    sb.append("Bad Address " + text + "\n");
            }
        }
        String error = sb.toString();
        if(error.length() == 0)
            return;
        throw new UnsupportedOperationException(error);
    }
}
