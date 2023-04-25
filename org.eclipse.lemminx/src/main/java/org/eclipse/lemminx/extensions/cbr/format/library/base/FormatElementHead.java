package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.settings.XMLFormattingOptions;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatElementHead extends Format {

    public FormatElementHead(DOMNode node, Context ctx, FormattingOrder order) {
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

    /**
     * Format the given DOM element
     *
     * @param element the DOM element to format.
     * @throws BadLocationException
     */
    private void formatElement(DOMElement element, XMLBuilder xmlBuilder) throws BadLocationException {
        String tag = element.getTagName();
        if (element.hasEndTag() && !element.hasStartTag()) {
            // bad element without start tag (ex: <\root>)
            return; // todo separate rule?
        } else {
            // generate start element
            xmlBuilder.startElement(tag, false);
            if (element.hasAttributes()) {
                ctx.formatAttributes(element, xmlBuilder);
            }

            XMLFormattingOptions.EmptyElements emptyElements = ctx.getEmptyElements(element);
            switch (emptyElements) {
                case expand:
                    // expand empty element: <example /> -> <example></example>
                    xmlBuilder.closeStartElement();
                    break;
                case collapse:
                    // collapse empty element: <example></example> -> <example />
                    break;
                default:
                    if (element.isStartTagClosed()) {
                        ctx.formatElementStartTagCloseBracket(element, xmlBuilder);
                    }
            }
        }
    }
}
