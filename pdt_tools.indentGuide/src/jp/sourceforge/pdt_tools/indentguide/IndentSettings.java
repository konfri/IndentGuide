package jp.sourceforge.pdt_tools.indentguide;

public class IndentSettings {
    private boolean drawBlankLine;
    private boolean skipBlockComment;
    private boolean drawLeftEnd;

    private IndentSettings(Builder b) {
        this.drawBlankLine = b.drawBlankLine;
        this.skipBlockComment = b.skipBlockComment;
        this.drawLeftEnd = b.drawLeftEnd;
    }

    public boolean drawBlankLine() {
        return drawBlankLine;
    }

    public boolean skipBlankLine() {
        return !drawBlankLine();
    }

    public boolean skipBlockComment() {
        return skipBlockComment;
    }

    public boolean drawBlockComment() {
        return !skipBlockComment();
    }

    public boolean drawFirst() {
        return drawLeftEnd;
    }

    public boolean skipFirst() {
        return !drawFirst();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean drawBlankLine = true;
        private boolean drawLeftEnd = true;
        private boolean skipBlockComment = true;

        public Builder drawBlankLine(boolean drawBlankLine) {
            this.drawBlankLine = drawBlankLine;
            return this;
        }

        public Builder drawLeftEnd(boolean drawLeftEnd) {
            this.drawLeftEnd = drawLeftEnd;
            return this;
        }

        public Builder skipBlockComment(boolean skipBlockComment) {
            this.skipBlockComment = skipBlockComment;
            return this;
        }

        public IndentSettings build() {
            return new IndentSettings(this);
        }
    }
}
