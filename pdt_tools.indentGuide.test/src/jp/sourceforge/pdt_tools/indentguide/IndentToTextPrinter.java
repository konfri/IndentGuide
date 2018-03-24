package jp.sourceforge.pdt_tools.indentguide;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IndentToTextPrinter {

    public static String print(String text, int... indentIndex) {
        List<Integer> boxed = Arrays.stream(indentIndex)
                .boxed()
                .collect(Collectors.toList());
        return print(text, boxed);
    }

    public static String print(String line, Iterable<Integer> indentIndex) {
        StringBuilder sb = new StringBuilder();
        int stringIndex = 0;
        for (int i : indentIndex) {

            while (stringIndex < i) {
                sb.append(line.charAt(stringIndex));
                stringIndex++;
            }

            sb.append("|");
        }

        sb.append(line.substring(stringIndex));

        return sb.toString();
    }
}
