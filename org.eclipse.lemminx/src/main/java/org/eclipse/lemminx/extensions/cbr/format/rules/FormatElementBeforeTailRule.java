package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatElementBeforeTail;

public class FormatElementBeforeTailRule extends SimpleFormatRule {
    public FormatElementBeforeTailRule() {
        super(RuleSequence.BEFORE_TAIL,
                DOMNode::isElement,
                node -> new FormatElementBeforeTail());
    }
}
