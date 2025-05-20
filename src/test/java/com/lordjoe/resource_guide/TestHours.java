package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.HoursHandler;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

/**
 * com.lordjoe.resource_guide.TestHours
 * User: Steve
 * Date: 5/8/25
 */
public class TestHours {
    public static void main(String[] args) throws Exception {
        File dir = new File("test");
        StringBuilder sb = new StringBuilder();

          if (dir.exists() && dir.isDirectory()) {
            File f = new File(dir, "hours.txt");
            LineNumberReader lnr = new LineNumberReader(new FileReader(f));
            String text = null;
            while ((text = lnr.readLine()) != null) {
                if (!HoursHandler.isHours(text))
                     sb.append("Bad Hours " + text + "/n");
            }
        }
        String error = sb.toString();
          if(error.length() == 0)
              return;
        throw new UnsupportedOperationException(error);
    }

}
