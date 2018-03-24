package jp.sourceforge.pdt_tools.indentguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockTextImpl implements IText {

    public static int DEFAULT_TAB_TO_SPACES = 4;

    private String filePath;
    private int tabToSpaces;

    private String[] lines;
    private Map<Integer, String> zippedLinesWithIndex;

    private MockTextImpl(Builder builder) {
        filePath = builder.filePath;
        tabToSpaces = builder.tabToSpaces;

        lines = readLines(filePath);
        zippedLinesWithIndex = zipLinesWithIndex(lines);
    }

    public static Builder builder(String filePath) {
        return new Builder(filePath);
    }

    @Override
    public String getLine(int lineNr) {
        return zippedLinesWithIndex.get(lineNr);
    }

    @Override
    public int getOffsetAtLine(int lineNr) {
        return IntStream.range(0, lineNr)
                .map(t -> lines[t].length() + 1)
                .sum();
    }

    @Override
    public int getLineCount() {
        return lines.length;
    }

    @Override
    public int getTabsToSpaces() {
        return tabToSpaces;
    }

    private static Map<Integer, String> zipLinesWithIndex(String[] lines) {
        return IntStream
                .range(0, lines.length)
                .mapToObj(i -> i)
                .collect(Collectors.toMap(i -> i, i -> lines[i]));
    }

    private static String[] readLines(String fileName) {
        try (InputStream is = MockTextImpl.class.getResourceAsStream(fileName)) {
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not read file %s", fileName), e);
        }
    }

    public static class Builder {
        private String filePath;
        private int tabToSpaces = DEFAULT_TAB_TO_SPACES;

        public Builder(String filePath) {
            this.filePath = filePath;
        }

        public Builder setTabToSpaceLenght(int tabToSpaces) {
            this.tabToSpaces = tabToSpaces;
            return this;
        }

        public MockTextImpl build() {
            return new MockTextImpl(this);
        }
    }

    public Map<Integer, String> getZippedLinesWithIndex() {
        return zippedLinesWithIndex;
    }

    public String getLines() {
        return String.join(System.getProperty("line.separator"), lines);
    }
}
