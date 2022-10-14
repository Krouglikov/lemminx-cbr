package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.utils.XMLBuilder;

public class AnotherNewLineAndIndentIfContextAllows extends ContextBoundFormat {

    @Override
    public void accept(DOMNode node, XMLBuilder xmlBuilder) {
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

