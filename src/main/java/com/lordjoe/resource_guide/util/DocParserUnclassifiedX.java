package com.lordjoe.resource_guide.util;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocParserUnclassifiedX {

    public static void parseDoc(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             HWPFDocument document = new HWPFDocument(fis)) {

            Range range = document.getRange();
            List<String> currentResourceLines = new ArrayList<>();

            for (int i = 0; i < range.numParagraphs(); i++) {
                Paragraph para = range.getParagraph(i);
                String text = para.text().replace('\u000B', '\n').trim();
               
                if (text.isEmpty()) continue;

                boolean isBold = false;
                for (int j = 0; j < para.numCharacterRuns(); j++) {
                    CharacterRun run = para.getCharacterRun(j);
                    if (run.text().trim().length() > 0 && run.isBold()) {
                        if (!currentResourceLines.isEmpty()) {
                            WordDocParserUnclassified.processResourceLines(currentResourceLines);
                            currentResourceLines.clear();
                        }
                    }
                        currentResourceLines.add(run.text()); // This is the name of a new resource
                   }
                }

            // Handle the last resource
            if (!currentResourceLines.isEmpty()) {
                WordDocParserUnclassified.processResourceLines(currentResourceLines);
            }
        }
    }

    private static void processResource(List<String> lines) {
        System.out.println("=== New Resource ===");
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("path/to/your/file.doc");
        parseDoc(file);
    }
}
