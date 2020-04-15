package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
//TODO  过滤和对讲
    public static void main(String[] args) {

        Map<String, String> prodIdMapedListPrice = new HashMap<>();
        try (FileReader fileReader = new FileReader(new File("./modification.properties"));
             BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                str = str.trim();
                String[] mapStr = str.split("\\s");
                if (mapStr.length == 2) {
                    prodIdMapedListPrice.put(mapStr[0], mapStr[1]);
                    System.out.println(str);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = args[0];
        File dir = new File(path);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("jmx") && !name.startsWith("deal_");
            }
        });
        for (File file : files) {
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader bufferedReader = new BufferedReader(fileReader);
                 FileWriter fileWriter = new FileWriter(new File(path + File.separator + "deal_" +file.getName()));
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
            ) {

                String str = null;
                String lastLine = null;
                String listPrice = null;
                while ((str = bufferedReader.readLine()) != null) {
                    if (listPrice != null) {
                        String oldListPrice = str.substring(str.indexOf(">") + 1,str.indexOf("<",str.indexOf("<") + 1));
                        str = str.replace(oldListPrice, listPrice);
                        listPrice = null;
                    }
                    if (lastLine != null && (str.contains(">Discount<") || str.contains("BottomPrice"))) {
                        String prodId = lastLine.substring(lastLine.indexOf(">") + 1,lastLine.indexOf("<",lastLine.indexOf("<") + 1));
                        listPrice = prodIdMapedListPrice.get(prodId);
                        str = str.replace("Discount", "BottomPrice");
                    }
                    bufferedWriter.write(str + "\n");
                    lastLine = str;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
