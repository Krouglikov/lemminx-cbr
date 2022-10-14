package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMProcessingInstruction;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatProcessingInstruction extends ContextBoundFormat {
    public FormatProcessingInstruction() {
        super();
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        formatProcessingInstruction(domNode, xmlBuilder);
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
