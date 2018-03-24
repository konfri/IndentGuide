package jp.sourceforge.pdt_tools.indentguide;

import java.util.ArrayList;
import java.util.List;

public class LineIndentCalculator {

    /**
     * Calculates line indents as drawing happens when the cursor is moved line by line.
     */
    public static Iterable<Integer> calculateLineIndents(IText text, int lineNr, IndentSettings indentSettings) {
        String textAsString = text.getLine(lineNr);
        int lineOffset = text.getOffsetAtLine(lineNr);
        int tabToSpaces = text.getTabsToSpaces();
        int extend = 0;
        if (indentSettings.isSkipBlockComment() && assumeCommentBlock(textAsString, tabToSpaces)) {
            extend -= tabToSpaces;
        }
        if (indentSettings.isDrawBlankLine() && textAsString.trim().length() == 0) {
            int prevLine = lineNr;
            while (--prevLine >= 0) {
                textAsString = text.getLine(prevLine);
                if (textAsString.trim().length() > 0) {
                    int postLine = lineNr;
                    int lineCount = text.getLineCount();
                    while (++postLine < lineCount) {
                        String tmp = text.getLine(postLine);
                        if (tmp.trim().length() > 0) {
                            if (countSpaces(textAsString, tabToSpaces) < countSpaces(tmp, tabToSpaces)) {
                                extend += tabToSpaces;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }

        int startOffset = indentSettings.isDrawLeftEnd() ? 0 : tabToSpaces;
        int endOffset = countSpaces(textAsString, tabToSpaces) + extend;

        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < textAsString.length() && Character.isWhitespace(textAsString.charAt(i)); i++) {
            switch (textAsString.charAt(i)) {
            case '\t':
                list.add(lineOffset + i);
                break;
            case ' ':
                boolean spaces = true;
                for (int j = i + 1; j < i + tabToSpaces; j++) {
                    if (textAsString.charAt(j) != ' ') {
                        spaces = false;
                    }
                }
                if (spaces) {
                    list.add(lineOffset + i);
                    i += tabToSpaces - 1;
                }
            }
        }

        if (removeFirstIndent(indentSettings)) {
            if (list.iterator().next() == 0) {
                list.remove(0);
            }
        }

        return list;
    }

    private static boolean removeFirstIndent(IndentSettings indentSettings) {
        return !indentSettings.isDrawLeftEnd();
    }

    private static int countSpaces(String str, int tabs) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
            case ' ':
                count++;
                break;
            case '\t':
                int z = tabs - count % tabs;
                count += z;
                break;
            default:
                return count;
            }
        }
        return count;
    }

    private static boolean assumeCommentBlock(String text, int tabs) {
        int count = countSpaces(text, tabs);
        count = (count / tabs) * tabs;
        int index = 0;
        for (int i = 0; i < count; i++) {
            switch (text.charAt(index)) {
            case ' ':
                index++;
                break;
            case '\t':
                index++;
                int z = tabs - i % tabs;
                i += z;
                break;
            default:
                i = count;
            }
        }
        text = text.substring(index);
        if (text.matches("^ \\*([ \\t].*|/.*|)$")) {
            return true;
        }
        return false;
    }
}
