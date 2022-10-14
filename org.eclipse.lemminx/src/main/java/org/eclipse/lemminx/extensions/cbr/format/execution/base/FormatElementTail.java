package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.settings.XMLFormattingOptions;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatElementTail extends ContextBoundFormat {

    public FormatElementTail() {
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
        String tag = element.getTagName();
        if (element.hasEndTag() && !element.hasStartTag()) {
            // bad element without start tag (ex: <\root>)
            xmlBuilder.endElement(tag, element.isEndTagClosed());
        } else {
            XMLFormattingOptions.EmptyElements emptyElements = ctx.getEmptyElements(element);
            switch (emptyElements) {
                case expand:
                    // end tag element is done, only if the element is closed
                    // the format, doesn't fix the close tag
                    xmlBuilder.endElement(tag, true);
                    break;
                case collapse:
                    // collapse empty element: <example></example> -> <example />
                    formatElementStartTagSelfCloseBracket(element, xmlBuilder);
                    break;
                default:
                    if (element.hasEndTag()) {
                        // end tag element is done, only if the element is closed
                        // the format, doesn't fix the close tag
                        if (element.hasEndTag() && element.getEndTagOpenOffset() <= ctx.endOffset) {
                            xmlBuilder.endElement(tag, element.isEndTagClosed());
                        } else {
                            formatElementStartTagSelfCloseBracket(element, xmlBuilder);
                        }
                    } else if (element.isSelfClosed()) {
                        formatElementStartTagSelfCloseBracket(element, xmlBuilder);
                    }
            }
        }
    }

    /**
     * Formats the self-closing tag (/>) according to
     * {@code XMLFormattingOptions#isPreserveAttrLineBreaks()}
     * <p>
     * {@code XMLFormattingOptions#isPreserveAttrLineBreaks()}: If true, must add a
     * newline + indent before the self-closing tag if the last attribute of the
     * element and the closing bracket are in different lines.
     *
     * @param element
     * @throws BadLocationException
     */
    private void formatElementStartTagSelfCloseBracket(DOMElement element, XMLBuilder xmlBuilder) throws BadLocationException {
        if (ctx.sharedSettings.getFormattingSettings().isPreserveAttrLineBreaks() && element.hasAttributes()) {
            int elementEndOffset = element.getEnd();
            if (element.isStartTagClosed()) {
                elementEndOffset = element.getStartTagCloseOffset();
            }
            if (!ctx.isSameLine(ctx.getLastAttribute(element).getEnd(), elementEndOffset)) {
                xmlBuilder.linefeed();
                xmlBuilder.indent(ctx.indentLevel);
            }
        }
        xmlBuilder.selfCloseElement();
    }

    /**
     * Formats the start tag's closing bracket (>) according to
     * {@code XMLFormattingOptions#isPreserveAttrLineBreaks()}
     * <p>
     * {@code XMLFormattingOptions#isPreserveAttrLineBreaks()}: If true, must add a
     * newline + indent before the closing bracket if the last attribute of the
     * element and the closing bracket are in different lines.
     *
     * @param element
     * @throws BadLocationException
     */
    private void formatElementStartTagCloseBracket(DOMElement element, XMLBuilder xmlBuilder) throws BadLocationException {
        if (ctx.sharedSettings.getFormattingSettings().isPreserveAttrLineBreaks() && element.hasAttributes()
                && !ctx.isSameLine(ctx.getLastAttribute(element).getEnd(), element.getStartTagCloseOffset())) {
            xmlBuilder.linefeed();
            xmlBuilder.indent(ctx.indentLevel);
        }
        xmlBuilder.closeStartElement();
    }


}
