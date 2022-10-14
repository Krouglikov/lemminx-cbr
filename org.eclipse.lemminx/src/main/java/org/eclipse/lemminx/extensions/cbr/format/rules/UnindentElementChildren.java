package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.DecreaseIndent;

public class UnindentElementChildren extends SimpleFormatRule {
    public UnindentElementChildren() {
        super(RuleSequence.AFTER_CHILDREN,
                DOMNode::isElement,
                node -> new DecreaseIndent());
    }
}
