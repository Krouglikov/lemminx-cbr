package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;
import org.eclipse.lemminx.settings.XMLFormattingOptions;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatElementBeforeTail extends NodeFormat {

    public FormatElementBeforeTail(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        try {
            formatElement((DOMElement) node, xmlBuilder);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private void formatElement(DOMElement element, XMLBuilder xmlBuilder) throws BadLocationException {
        if (element.hasEndTag() && !element.hasStartTag()) {
            // bad element without start tag (ex: <\root>)
            return;
        }
        XMLFormattingOptions.EmptyElements emptyElements = ctx.getEmptyElements(element);
        switch (emptyElements) {
            case expand:
            case collapse:
                break;
            default: //TODO упростить формат, вынести условия в правило
                boolean hasElements = Predicates.hasNonTextChildren().test(element);
                if (element.hasEndTag() && hasElements) {
                    xmlBuilder.linefeed();
                    xmlBuilder.indent(ctx.indentLevel);
                }
        }
    }
}
