package de.dofe.ev3.util;

import java.io.*;

public class FileUtils {

    public static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static void writeToFile(String content, String fileName) {
        try {
            try (FileWriter fw = new FileWriter(fileName)) {
                fw.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
