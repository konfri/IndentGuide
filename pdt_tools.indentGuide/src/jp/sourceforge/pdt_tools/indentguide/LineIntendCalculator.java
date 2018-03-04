package jp.sourceforge.pdt_tools.indentguide;

import java.util.Arrays;

import org.eclipse.swt.custom.StyledText;

public class LineIntendCalculator {

    public Iterable<Integer> calculate(StyledText styledText, int lineNr, int tabToSpaces, IndentSettings indentSettings) {
        String text = styledText.getLine(lineNr);

        int extend = 0;
        if (indentSettings.isSkipCommentBlock() && assumeCommentBlock(text, tabToSpaces)) {
            extend -= tabToSpaces;
        }
        if (indentSettings.isDrawBlankLine() && text.trim().length() == 0) {
            int prevLine = lineNr;
            while (--prevLine >= 0) {
                text = styledText.getLine(prevLine);
                if (text.trim().length() > 0) {
                    int postLine = lineNr;
                    int lineCount = styledText.getLineCount();
                    while (++postLine < lineCount) {
                        String tmp = styledText.getLine(postLine);
                        if (tmp.trim().length() > 0) {
                            if (countSpaces(text, tabToSpaces) < countSpaces(tmp, tabToSpaces)) {
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
        int endOffset = countSpaces(text, tabToSpaces) + extend;

        return Arrays.asList(92,93,94,95); // dummy values
    }

    private int countSpaces(String str, int tabs) {
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

    private boolean assumeCommentBlock(String text, int tabs) {
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
