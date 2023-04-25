package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;

public class IncreaseIndent extends Format {
    public IncreaseIndent(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        ctx.indentLevel++;
    }
}
