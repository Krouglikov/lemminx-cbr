package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;

public class NewLineIfContextDemands extends NodeFormat {
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
