package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.AnotherNewLineAndIndentIfContextAllows;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatText;

public class OverrideTextFormat extends OverrideDitaChildFormat {

    public OverrideTextFormat() {
        super(AnotherNewLineAndIndentIfContextAllows.class, FormatText.class);
    }

    @Override
    protected String getContent(DOMNode node, Context ctx) {
        return ((DOMText) node).getData();
    }

    @Override
    protected String getDelimiter(DOMNode node) {
        return ((DOMText) node).getDelimiter();
    }

}
