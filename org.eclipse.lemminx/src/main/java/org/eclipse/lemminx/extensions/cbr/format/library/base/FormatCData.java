package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.dom.DOMCDATASection;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatCData extends Format {

    public FormatCData(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        formatCDATA((DOMCDATASection) node, xmlBuilder);
    }

    /**
     * Format the given DOM CDATA
     *
     * @param cdata the DOM CDATA to format.
     */
    private void formatCDATA(DOMCDATASection cdata, XMLBuilder xmlBuilder) {
        xmlBuilder.startCDATA();
        xmlBuilder.addContentCDATA(cdata.getData());
        if (cdata.isClosed()) {
            // Generate ]> only if CDATA is closed.
            xmlBuilder.endCDATA();
        }
    }
}