package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatElementTail;

public class FormatElementTailRule extends SimpleFormatRule {
    public FormatElementTailRule() {
        super(RuleSequence.TAIL,
                DOMNode::isElement,
                node -> new FormatElementTail());
    }
}
