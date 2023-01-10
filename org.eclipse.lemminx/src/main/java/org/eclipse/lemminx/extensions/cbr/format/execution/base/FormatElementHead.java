package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.settings.XMLFormattingOptions;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatElementHead extends ContextBoundFormat {

    public FormatElementHead() {
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
