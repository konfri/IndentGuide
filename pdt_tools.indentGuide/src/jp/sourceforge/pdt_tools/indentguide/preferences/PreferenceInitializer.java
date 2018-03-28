package jp.sourceforge.pdt_tools.indentguide.preferences;

import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;

import jp.sourceforge.pdt_tools.indentguide.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.ENABLED, true);
        store.setDefault(PreferenceConstants.LINE_ALPHA, 50);
        store.setDefault(PreferenceConstants.LINE_STYLE, SWT.LINE_SOLID);
        store.setDefault(PreferenceConstants.LINE_WIDTH, 1);
        store.setDefault(PreferenceConstants.LINE_SHIFT, 3);
        store.setDefault(PreferenceConstants.LINE_COLOR, "0,0,0"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.DRAW_LEFT_END, true);
        store.setDefault(PreferenceConstants.DRAW_BLANK_LINE, false);
        store.setDefault(PreferenceConstants.SKIP_COMMENT_BLOCK, false);
        store.setDefault(PreferenceConstants.CONTENT_TYPES,
                IContentTypeManager.CT_TEXT);
    }

}
