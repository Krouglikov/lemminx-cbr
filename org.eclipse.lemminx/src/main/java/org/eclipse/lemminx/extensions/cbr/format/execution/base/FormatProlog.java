package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMProcessingInstruction;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.List;

public class FormatProlog extends NodeFormat {

    public FormatProlog(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        formatProlog(node, xmlBuilder);
    }

    /**
     * Format the given DOM prolog
     *
     * @param node the DOM prolog to format.
     */
    private void formatProlog(DOMNode node, XMLBuilder xmlBuilder) {
        addPrologToXMLBuilder(node, xmlBuilder);
        ctx.linefeedOnNextWrite = true;
    }

    public static void addPrologToXMLBuilder(DOMNode node, XMLBuilder xml) {
        DOMProcessingInstruction processingInstruction = (DOMProcessingInstruction) node;
        xml.startPrologOrPI(processingInstruction.getTarget());
        if (node.hasAttributes()) {
            addPrologAttributes(node, xml);
        }
        xml.endPrologOrPI();
    }


    /**
     * Will add all attributes, to the given builder, on a single line
     */
    private static void addPrologAttributes(DOMNode node, XMLBuilder xmlBuilder) {
        List<DOMAttr> attrs = node.getAttributeNodes();
        if (attrs == null) {
            return;
        }
        for (DOMAttr attr : attrs) {
            xmlBuilder.addPrologAttribute(attr);
        }
    }

}
