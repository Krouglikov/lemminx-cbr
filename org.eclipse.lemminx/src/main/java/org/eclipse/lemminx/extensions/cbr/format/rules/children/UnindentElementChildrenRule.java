package org.eclipse.lemminx.extensions.cbr.format.rules.children;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.DecreaseIndent;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

public class UnindentElementChildrenRule extends SimpleFormatRule {
    public UnindentElementChildrenRule() {
        super(FormattingOrder.AFTER_CHILDREN,
                DOMNode::isElement,
                node -> new DecreaseIndent());
    }
}
