package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.IncreaseIndent;

public class IndentElementChildren extends SimpleFormatRule {
    public IndentElementChildren() {
        super(RuleSequence.BEFORE_CHILDREN,
                DOMNode::isElement,
                node -> new IncreaseIndent());
    }
}
