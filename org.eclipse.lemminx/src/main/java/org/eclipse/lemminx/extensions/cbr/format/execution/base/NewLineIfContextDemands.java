package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.utils.XMLBuilder;

public class NewLineIfContextDemands extends ContextBoundFormat {
    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        if (ctx.linefeedOnNextWrite) {
            xmlBuilder.linefeed();
            ctx.linefeedOnNextWrite = false;
        }
    }
}
