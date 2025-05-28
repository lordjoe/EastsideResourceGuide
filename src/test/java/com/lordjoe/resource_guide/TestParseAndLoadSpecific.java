package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.DatabaseConnection;
import com.lordjoe.resource_guide.util.WordDocParser;

import java.io.File;
import java.util.List;

/**
 * com.lordjoe.resource_guide.TestParseAndLoadSpecific
 * User: Steve
 * Date: 5/23/25
 */
public class TestParseAndLoadSpecific {
    public static final TestParseAndLoadSpecific[] EMPTY_ARRAY = {};


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            return;
        }
        DatabaseConnection.setTEST();
        if (true) {
            Database.clearDatabase();
            Database.createDatabase();
            File dir = new File("test");
            if (dir.exists() && dir.isDirectory()) {
                File f = new File(dir, args[0]);
                WordDocParser.parseAndInsertDocx(f);
            }
        }
        Guide guide = Guide.Instance;
        guide.guaranteeLoaded();
        List<Catagory> catagories = guide.getCatagories();

    }
}

