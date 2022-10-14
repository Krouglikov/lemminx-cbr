package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMComment;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.utils.XMLBuilder;

public class FormatComment extends ContextBoundFormat {
    public FormatComment() {
        super();
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        formatComment((DOMComment) domNode, xmlBuilder);
    }

    /**
     * Format the given DOM Comment
     *
     * @param comment the DOM Comment to format.
     */
    private void formatComment(DOMComment comment, XMLBuilder xmlBuilder) {
        xmlBuilder.startComment(comment);
        xmlBuilder.addContentComment(comment.getData());
        if (comment.isClosed()) {
            // Generate --> only if comment is closed.
            xmlBuilder.endComment();
        }
        if (ctx.indentLevel == 0) {
            ctx.linefeedOnNextWrite = true;
        }
    }
}
