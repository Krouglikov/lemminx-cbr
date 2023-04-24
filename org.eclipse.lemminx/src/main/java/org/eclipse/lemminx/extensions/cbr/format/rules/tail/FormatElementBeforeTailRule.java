package org.eclipse.lemminx.extensions.cbr.format.rules.tail;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatElementBeforeTail;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Элементы перед хвостовиком
 */
public class FormatElementBeforeTailRule extends SimpleFormatRule {
    public FormatElementBeforeTailRule() {
        super(FormattingOrder.BEFORE_TAIL,
                DOMNode::isElement,
                node -> new FormatElementBeforeTail()); //todo rework -- complex logic
    }
}
