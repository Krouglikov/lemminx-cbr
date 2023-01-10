package org.eclipse.lemminx.extensions.cbr.format.rules.tail;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatElementTail;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Формат хвоста элемента
 */
public class FormatElementTailRule extends SimpleFormatRule {
    public FormatElementTailRule() {
        super(FormattingSequence.TAIL,
                DOMNode::isElement,
                node -> new FormatElementTail()); //todo reork -- complex logic
    }
}
