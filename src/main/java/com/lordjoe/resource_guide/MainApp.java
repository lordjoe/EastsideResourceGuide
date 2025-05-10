package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.DocParserUnclassifiedX;
import com.lordjoe.resource_guide.util.WordDocParser;

import java.io.File;

public class MainApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: MainApp <path_to_docx>");
            return;
        }
  //      if(DatabaseConnection.isRemote()) {
  //          throw new UnsupportedOperationException("Only Local "); // ToDo
  //      }

        String filepath = args[0];
        try {
            Database.clearDatabase();
            Database.createDatabase();
            File dir = new File(filepath);
            if(dir.exists() && dir.isDirectory())   {
                File[] files = dir.listFiles();
                if(files!=null) {
                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        if (f.getName().startsWith(".~lock"))
                            continue;

                        System.out.println("Handling file " + f.getName());
                        if(f.getName().contains("Emerald")) {
                            DocParserUnclassifiedX.parseDoc(f);
                        }
                        else {
                            WordDocParser.parseAndInsertDocx(f);

                        }
                     }
                }
            }
              System.out.println("Document parsed and inserted successfully.");
          //  Database.validateAllURLs();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
