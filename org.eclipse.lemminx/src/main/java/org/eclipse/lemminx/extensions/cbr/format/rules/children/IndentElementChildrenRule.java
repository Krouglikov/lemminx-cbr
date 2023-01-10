package org.eclipse.lemminx.extensions.cbr.format.rules.children;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.IncreaseIndent;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Перед вложенными элементами увеличивается отступ
 */
public class IndentElementChildrenRule extends SimpleFormatRule {
    public IndentElementChildrenRule() {
        super(FormattingSequence.BEFORE_CHILDREN,
                DOMNode::isElement,
                node -> new IncreaseIndent());
    }
}
