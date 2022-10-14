package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatComment;

public class FormatCommentRule extends SimpleFormatRule {
    public FormatCommentRule() {
        super(1,
                DOMNode::isComment,
                node -> new FormatComment());
    }
}
