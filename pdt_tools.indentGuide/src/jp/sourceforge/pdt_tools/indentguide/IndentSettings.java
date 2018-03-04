package jp.sourceforge.pdt_tools.indentguide;

import org.eclipse.jface.preference.IPreferenceStore;

import jp.sourceforge.pdt_tools.indentguide.preferences.PreferenceConstants;

public class IndentSettings {
    private boolean drawBlankLine;
    private boolean skipCommentBlock;
    private boolean drawLeftEnd;

    public IndentSettings() {
        IPreferenceStore ps = Activator.getDefault().getPreferenceStore();
        drawBlankLine = ps.getBoolean(PreferenceConstants.DRAW_BLANK_LINE);
        skipCommentBlock = ps.getBoolean(PreferenceConstants.SKIP_COMMENT_BLOCK);
        drawLeftEnd = ps.getBoolean(PreferenceConstants.DRAW_LEFT_END);
    }

    public boolean isDrawBlankLine() {
        return drawBlankLine;
    }

    public boolean isSkipCommentBlock() {
        return skipCommentBlock;
    }

    public boolean isDrawLeftEnd() {
        return drawLeftEnd;
    }
}
