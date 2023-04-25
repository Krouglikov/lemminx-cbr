package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.library.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.utils.LogToFile;

public class AnotherNewLineAndIndentIfIndented extends Format {

    public AnotherNewLineAndIndentIfIndented(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        if (ctx.indentLevel > 0) {
            // add new line + indent
            if (!node.isChildOfOwnerDocument() || node.getPreviousNonTextSibling() != null) {
                xmlBuilder.linefeed();
            }

            if (!Predicates.startTagExistsInRangeDocument().test(node)
                    && Predicates.startTagExistsInFullDocument(ctx).test(node)) {
                DOMNode startNode = ctx.getFullDocElemFromRangeElem((DOMElement) node);
                int currentIndentLevel;
                try {
                    currentIndentLevel = ctx.getNodeIndentLevel(startNode);
                } catch (BadLocationException e) {
                    currentIndentLevel = 0;
                }
                xmlBuilder.indent(currentIndentLevel);
                ctx.indentLevel = currentIndentLevel;
            } else {
                xmlBuilder.indent(ctx.indentLevel);
            }
        }
    }

}

