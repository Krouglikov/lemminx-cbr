package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatElementHead;

public class FormatElementHeadRule extends SimpleFormatRule {
    public FormatElementHeadRule() {
        super(RuleSequence.HEAD,
                DOMNode::isElement,
                node -> new FormatElementHead());
    }
}
