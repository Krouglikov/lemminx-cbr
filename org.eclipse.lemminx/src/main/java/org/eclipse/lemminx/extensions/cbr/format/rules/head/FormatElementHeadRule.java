package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatElementHead;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Формат головы элемента
 */
public class FormatElementHeadRule extends SimpleFormatRule {
    public FormatElementHeadRule() {
        super(FormattingSequence.HEAD,
                DOMNode::isElement,
                node -> new FormatElementHead()); //todo rework -- complex logic
    }
}
