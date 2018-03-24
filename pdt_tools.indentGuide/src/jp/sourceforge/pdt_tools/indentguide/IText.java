package jp.sourceforge.pdt_tools.indentguide;

public interface IText {

    String getLine(int lineNr);

    int getOffsetAtLine(int lineNr);

    int getLineCount();

    int getTabsToSpaces();

}
