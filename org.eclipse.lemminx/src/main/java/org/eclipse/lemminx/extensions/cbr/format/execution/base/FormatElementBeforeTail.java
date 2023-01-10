package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.settings.XMLFormattingOptions;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatElementBeforeTail extends ContextBoundFormat {

    public FormatElementBeforeTail() {
        super();
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        try {
            formatElement((DOMElement) domNode, xmlBuilder);
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
