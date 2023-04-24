package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMProcessingInstruction;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatProcessingInstruction extends NodeFormat {

    public FormatProcessingInstruction(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        formatProcessingInstruction(node, xmlBuilder);
    }

    /**
     * Format the given DOM ProcessingIntsruction.
     *
     * @param node the DOM ProcessingIntsruction to format.
     */
    private void formatProcessingInstruction(DOMNode node, XMLBuilder xmlBuilder) {
        addPIToXMLBuilder(node, xmlBuilder);
        if (ctx.indentLevel == 0) {
            xmlBuilder.linefeed();
        }
    }

    private static void addPIToXMLBuilder(DOMNode node, XMLBuilder xml) {
        DOMProcessingInstruction processingInstruction = (DOMProcessingInstruction) node;
        xml.startPrologOrPI(processingInstruction.getTarget());

        String content = processingInstruction.getData();
        if (content.length() > 0) {
            xml.addContentPI(content);
        } else {
            xml.addContent(" ");
        }

        xml.endPrologOrPI();
    }
}
