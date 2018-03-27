/*******************************************************************************
 * Copyright (c) 2006, 2009 Wind River Systems, Inc., IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Anton Leherbauer (Wind River Systems) - initial API and implementation - https://bugs.eclipse.org/bugs/show_bug.cgi?id=22712
 *     Anton Leherbauer (Wind River Systems) - [painting] Long lines take too long to display when "Show Whitespace Characters" is enabled - https://bugs.eclipse.org/bugs/show_bug.cgi?id=196116
 *     Anton Leherbauer (Wind River Systems) - [painting] Whitespace characters not drawn when scrolling to right slowly - https://bugs.eclipse.org/bugs/show_bug.cgi?id=206633
 *     Tom Eicher (Avaloq Evolution AG) - block selection mode
 *******************************************************************************/
package jp.sourceforge.pdt_tools.indentguide;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;

import jp.sourceforge.pdt_tools.indentguide.preferences.PreferenceConstants;

/**
 * A painter for drawing visible characters for (invisible) whitespace
 * characters.
 *
 * @since 3.3
 * @see org.eclipse.jface.text.WhitespaceCharacterPainter
 */
public class IndentGuidePainter implements IPainter, PaintListener {

    /** Indicates whether this painter is active. */
    private boolean fIsActive = false;
    /** The source viewer this painter is attached to. */
    private ITextViewer fTextViewer;
    /** The viewer's widget. */
    private StyledText fTextWidget;
    /** Tells whether the advanced graphics sub system is available. */
    private final boolean fIsAdvancedGraphicsPresent;

    private int lineAlpha;
    private int lineStyle;
    private int lineWidth;
    private int lineShift;
    private int spaceWidth;

    private IndentSettings indentSettings;

    /**
     * Creates a new painter for the given text viewer.
     *
     * @param textViewer
     *            the text viewer the painter should be attached to
     */
    public IndentGuidePainter(ITextViewer textViewer) {
        super();

        fTextViewer = textViewer;
        fTextWidget = textViewer.getTextWidget();
        GC gc = new GC(fTextWidget);
        gc.setAdvanced(true);
        fIsAdvancedGraphicsPresent = gc.getAdvanced();
        gc.dispose();

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        lineAlpha = store.getInt(PreferenceConstants.LINE_ALPHA);
        lineStyle = store.getInt(PreferenceConstants.LINE_STYLE);
        lineWidth = store.getInt(PreferenceConstants.LINE_WIDTH);
        lineShift = store.getInt(PreferenceConstants.LINE_SHIFT);

        this.indentSettings = IndentSettings.builder()
                .drawBlankLine(store.getBoolean(PreferenceConstants.DRAW_BLANK_LINE))
                .drawLeftEnd(store.getBoolean(PreferenceConstants.DRAW_LEFT_END))
                .skipBlockComment(store.getBoolean(PreferenceConstants.SKIP_COMMENT_BLOCK))
                .build();
    }

    /*
     * @see org.eclipse.jface.text.IPainter#dispose()
     */
    public void dispose() {
        fTextViewer = null;
        fTextWidget = null;
    }

    /*
     * @see org.eclipse.jface.text.IPainter#paint(int)
     */
    public void paint(int reason) {
        IDocument document = fTextViewer.getDocument();
        if (document == null) {
            deactivate(false);
            return;
        }
        if (!fIsActive) {
            fIsActive = true;
            fTextWidget.addPaintListener(this);
            redrawAll();
        } else if (reason == CONFIGURATION || reason == INTERNAL) {
            redrawAll();
        } else if (reason == TEXT_CHANGE) {
            // redraw current line only
            try {
                IRegion lineRegion = document
                        .getLineInformationOfOffset(getDocumentOffset(fTextWidget.getCaretOffset()));
                int widgetOffset = getWidgetOffset(lineRegion.getOffset());
                int charCount = fTextWidget.getCharCount();
                int redrawLength = Math.min(lineRegion.getLength(), charCount - widgetOffset);
                if (widgetOffset >= 0 && redrawLength > 0) {
                    fTextWidget.redrawRange(widgetOffset, redrawLength, true);
                }
            } catch (BadLocationException e) {
                // ignore
            }
        }
    }

    /*
     * @see org.eclipse.jface.text.IPainter#deactivate(boolean)
     */
    public void deactivate(boolean redraw) {
        if (fIsActive) {
            fIsActive = false;
            fTextWidget.removePaintListener(this);
            if (redraw) {
                redrawAll();
            }
        }
    }

    /*
     * @see org.eclipse.jface.text.IPainter#setPositionManager(org.eclipse.jface.
     * text.IPaintPositionManager)
     */
    public void setPositionManager(IPaintPositionManager manager) {
        // no need for a position manager
    }

    /*
     * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events
     * .PaintEvent)
     */
    public void paintControl(PaintEvent event) {
        if (fTextWidget != null) {
            handleDrawRequest(event.gc, event.x, event.y, event.width, event.height);
        }
    }

    /*
     * Draw characters in view range.
     */
    private void handleDrawRequest(GC gc, int x, int y, int w, int h) {
        int startLine = fTextWidget.getLineIndex(y);
        int endLine = fTextWidget.getLineIndex(y + h - 1);
        if (startLine <= endLine && startLine < fTextWidget.getLineCount()) {
            Color fgColor = gc.getForeground();
            LineAttributes lineAttributes = gc.getLineAttributes();
            gc.setForeground(Activator.getDefault().getColor());
            gc.setLineStyle(lineStyle);
            gc.setLineWidth(lineWidth);
            spaceWidth = gc.getAdvanceWidth(' ');
            if (fIsAdvancedGraphicsPresent) {
                int alpha = gc.getAlpha();
                gc.setAlpha(this.lineAlpha);
                drawLineRange(gc, startLine, endLine);
                gc.setAlpha(alpha);
            } else {
                drawLineRange(gc, startLine, endLine);
            }
            gc.setForeground(fgColor);
            gc.setLineAttributes(lineAttributes);
        }
    }

    /**
     * Draw the given line range.
     *
     * @param gc
     *            the GC
     * @param startLine
     *            first line number
     * @param endLine
     *            last line number (inclusive)
     * @param x
     *            the X-coordinate of the drawing range
     * @param w
     *            the width of the drawing range
     */
    private void drawLineRange(GC gc, int startLine, int endLine) {
        IText text = new IText() {
            @Override
            public int getOffsetAtLine(int lineNr) {
                return fTextWidget.getOffsetAtLine(lineNr);
            }

            @Override
            public int getLineCount() {
                return fTextWidget.getLineCount();
            }

            @Override
            public String getLine(int lineNr) {
                return fTextWidget.getLine(lineNr);
            }

            @Override
            public int getTabsToSpaces() {
                return fTextWidget.getTabs();
            }
        };

        for (int lineNr = startLine; lineNr <= endLine; lineNr++) {
            if (isBlockCollapsedAtLine(lineNr)) {
                continue;
            }
            
            for (Integer integer : LineIndentCalculator.calculateLineIndents(text, lineNr, indentSettings)) {
                draw(gc, integer);
            }
        }
    }

    private boolean isBlockCollapsedAtLine(int line) {
        if (fTextViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension = (ITextViewerExtension5) fTextViewer;
            int modelLine = extension.widgetLine2ModelLine(line);
            int widgetLine2 = extension.modelLine2WidgetLine(modelLine + 1);
            return widgetLine2 == -1;
        }
        return false;
    }

    /**
     * Redraw all of the text widgets visible content.
     */
    private void redrawAll() {
        fTextWidget.redraw();
    }

    /**
     *
     * @param gc
     * @param offset
     */
    private void draw(GC gc, int offset) {
        Point pos = fTextWidget.getLocationAtOffset(offset);
        gc.drawLine(pos.x + 1, pos.y, pos.x + 1, pos.y + fTextWidget.getLineHeight(offset));
    }

    /**
     * Convert a document offset to the corresponding widget offset.
     *
     * @param documentOffset
     *            the document offset
     * @return widget offset
     */
    private int getWidgetOffset(int documentOffset) {
        if (fTextViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension = (ITextViewerExtension5) fTextViewer;
            return extension.modelOffset2WidgetOffset(documentOffset);
        }
        IRegion visible = fTextViewer.getVisibleRegion();
        int widgetOffset = documentOffset - visible.getOffset();
        if (widgetOffset > visible.getLength()) {
            return -1;
        }
        return widgetOffset;
    }

    /**
     * Convert a widget offset to the corresponding document offset.
     *
     * @param widgetOffset
     *            the widget offset
     * @return document offset
     */
    private int getDocumentOffset(int widgetOffset) {
        if (fTextViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension = (ITextViewerExtension5) fTextViewer;
            return extension.widgetOffset2ModelOffset(widgetOffset);
        }
        IRegion visible = fTextViewer.getVisibleRegion();
        if (widgetOffset > visible.getLength()) {
            return -1;
        }
        return widgetOffset + visible.getOffset();
    }
}
