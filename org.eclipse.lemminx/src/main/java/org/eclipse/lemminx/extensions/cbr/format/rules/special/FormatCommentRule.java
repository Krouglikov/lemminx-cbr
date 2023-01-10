package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatComment;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Комментарии форматируются по своим собственным правилам
 */
public class FormatCommentRule extends SimpleFormatRule {
    public FormatCommentRule() {
        super(1,
                DOMNode::isComment,
                node -> new FormatComment());
    }
}
