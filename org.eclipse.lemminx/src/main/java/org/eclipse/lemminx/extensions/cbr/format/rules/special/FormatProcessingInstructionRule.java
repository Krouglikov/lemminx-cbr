package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatProcessingInstruction;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Инструкции форматируются по собственным правилам
 */
public class FormatProcessingInstructionRule extends SimpleFormatRule {
    public FormatProcessingInstructionRule() {
        super(1,
                DOMNode::isProcessingInstruction,
                node -> new FormatProcessingInstruction());
    }
}
