package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.utils.LogToFile;

public class NewLineIfContextDemands extends Format {
    public NewLineIfContextDemands(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        if (ctx.linefeedOnNextWrite) {
            xmlBuilder.linefeed();
            ctx.linefeedOnNextWrite = false;
        }
    }
}
