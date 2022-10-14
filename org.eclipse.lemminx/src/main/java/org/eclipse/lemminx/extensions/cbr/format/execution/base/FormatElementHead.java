package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.settings.XMLFormattingOptions;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.List;

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
                formatAttributes(element, xmlBuilder);
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
                        formatElementStartTagCloseBracket(element, xmlBuilder);
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

    private void formatAttributes(DOMElement element, XMLBuilder xmlBuilder) throws BadLocationException {
        List<DOMAttr> attributes = element.getAttributeNodes();
        boolean isSingleAttribute = ctx.hasSingleAttributeInFullDoc(element);
        int prevOffset = element.getStart();
        for (DOMAttr attr : attributes) {
            formatAttribute(attr, isSingleAttribute, prevOffset, xmlBuilder);
            prevOffset = attr.getEnd();
        }
        XMLFormattingOptions options = ctx.sharedSettings.getFormattingSettings();
        if ((options.getClosingBracketNewLine()
                && options.isSplitAttributes())
                && !isSingleAttribute) {
            xmlBuilder.linefeed();
            // Indent by tag + splitAttributesIndentSize to match with attribute indent level
            int totalIndent = ctx.indentLevel + options.getSplitAttributesIndentSize();
            xmlBuilder.indent(totalIndent);
        }
    }

    //todo separate formatter?
    private void formatAttribute(DOMAttr attr, boolean isSingleAttribute, int prevOffset, XMLBuilder xmlBuilder)
            throws BadLocationException {
        if (ctx.sharedSettings.getFormattingSettings().isPreserveAttrLineBreaks()
                && !ctx.isSameLine(prevOffset, attr.getStart())) {
            xmlBuilder.linefeed();
            xmlBuilder.indent(ctx.indentLevel + 1);
            xmlBuilder.addSingleAttribute(attr, false, false);
        } else if (isSingleAttribute) {
            xmlBuilder.addSingleAttribute(attr);
        } else {
            xmlBuilder.addAttribute(attr, ctx.indentLevel);
        }
    }


}
