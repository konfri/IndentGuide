package jp.sourceforge.pdt_tools.indentguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileReader {
    public static String[] readLines(String fileName) {
        try (InputStream is = FileReader.class.getResourceAsStream(fileName)) {
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not read file %s", fileName), e);
        }
    }

    public static CharSequence read(String fileName) {
        return String.join(System.getProperty("line.separator"), readLines(fileName));
    }
}
