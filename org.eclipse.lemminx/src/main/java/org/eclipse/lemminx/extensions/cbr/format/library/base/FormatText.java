package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatText extends Format {

    public FormatText(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        formatText((DOMText) node, xmlBuilder);
    }

    /**
     * Format the given DOM text node.
     *
     * @param textNode the DOM text node to format.
     */
    private void formatText(DOMText textNode, XMLBuilder xmlBuilder) {
        String content = textNode.getData();
        if (textNode.equals(ctx.fullDomDocument.getLastChild())) {
            xmlBuilder.addContent(content);
        } else {
            xmlBuilder.addContent(content, textNode.isWhitespace(), textNode.hasSiblings(),
                    textNode.getDelimiter());
        }
    }
}
