package jp.sourceforge.pdt_tools.indentguide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LineIndentCalculator {

    /**
     * Calculates line indents as drawing happens when the cursor is moved line by line.
     */
    public static List<Integer> calculateLineIndents(IText text, int lineNr, IndentSettings indentSettings) {
        String lineAsString = text.getLine(lineNr);
        int lineOffset = text.getOffsetAtLine(lineNr);
        int tabToSpaces = text.getTabsToSpaces();

        if( isLineBlank(lineAsString) && indentSettings.skipBlankLine()) {
            return Collections.emptyList();
        } else if( isLineBlank(lineAsString) && indentSettings.drawBlankLine()) {
            if( lineNr == 0) {
                return Collections.emptyList();
            }

            List<Integer> values = calculateLineIndents(text, lineNr - 1, indentSettings);
            if( values.isEmpty()) {
                return calculateFirst(indentSettings, lineAsString, lineOffset);
            }

            return values.stream()
                    .map(t -> t + (text.getOffsetAtLine(lineNr) - text.getOffsetAtLine(lineNr - 1)))
                    .collect(Collectors.toList());
        }

        List<Integer> list = calculateFirst(indentSettings, lineAsString, lineOffset);

        for (int i = 0; (i < lineAsString.length()) && Character.isWhitespace(lineAsString.charAt(i)); i++) {

            switch (lineAsString.charAt(i)) {
            case '\t':
                if( isNextCharWhitespace(lineAsString, i)) {
                    list.add(lineOffset + i + 1);
                }
                break;
            case ' ':
                if( enoughSpacesForTab(lineAsString, tabToSpaces, i)
                        && isNextCharWhitespace(lineAsString, (i + tabToSpaces) - 1)) {

                    list.add(lineOffset + i + tabToSpaces);
                    i += tabToSpaces - 1;
                }
            }
        }

        if( !list.isEmpty()
                && isBlockComment(lineAsString, tabToSpaces, list.get(list.size() - 1) - lineOffset)
                && indentSettings.skipBlockComment()) {
            list.remove(list.size() - 1);
        }

        return list;
    }

    private static List<Integer> calculateFirst(IndentSettings indentSettings, String lineAsString, int lineOffset) {
        List<Integer> list = new ArrayList<>();
        if( indentSettings.drawFirst() &&
                (isFirstCharAnWhitespace(lineAsString) || isLineBlank(lineAsString))) {
            list.add(lineOffset);
        }
        return list;
    }

    private static boolean isBlockComment(String lineAsString, int tabToSpaces, Integer lastIndentLineOffset) {
        if( enoughSpacesForTab(lineAsString, tabToSpaces, lastIndentLineOffset)) {
            return false;
        }

        for (int i = lastIndentLineOffset; i < lineAsString.length(); i++) {
            if( Character.isWhitespace(lineAsString.charAt(i))) {
                continue;
            }

            if( lineAsString.charAt(i) == '*') {
                return true;
            }

        }

        return false;
    }

    private static boolean isNextCharWhitespace(String lineAsString, int offset) {
        if( lineAsString.length() <= (offset + 1)) {
            return false;
        }

        return Character.isWhitespace(lineAsString.charAt(offset + 1));
    }

    private static boolean isFirstCharAnWhitespace(String lineAsString) {
        if( (lineAsString == null) || lineAsString.isEmpty()) {
            return false;
        }

        return Character.isWhitespace(lineAsString.charAt(0));
    }

    private static boolean isLineBlank(String lineAsString) {
        if( (lineAsString == null) || lineAsString.isEmpty()) {
            return true;
        }
        for (int i = 0; i < lineAsString.length(); i++) {
            if( !Character.isWhitespace(lineAsString.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean enoughSpacesForTab(String textAsString, int tabToSpaces, int i) {
        boolean spaces = true;
        for (int j = i + 1; j < (i + tabToSpaces); j++) {
            if( textAsString.length() <= j) {
                return false;
            }
            if( textAsString.charAt(j) != ' ') {
                spaces = false;
            }
        }
        return spaces;
    }
}
