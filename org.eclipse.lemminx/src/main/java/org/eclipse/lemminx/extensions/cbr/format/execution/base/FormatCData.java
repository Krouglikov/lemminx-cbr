package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMCDATASection;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatCData extends ContextBoundFormat {
    public FormatCData() {
        super();
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        formatCDATA((DOMCDATASection) domNode, xmlBuilder);
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
