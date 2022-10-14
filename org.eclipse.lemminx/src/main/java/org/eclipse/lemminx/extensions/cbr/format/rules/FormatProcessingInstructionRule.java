package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatProcessingInstruction;

public class FormatProcessingInstructionRule extends SimpleFormatRule {
    public FormatProcessingInstructionRule() {
        super(1,
                DOMNode::isProcessingInstruction,
                node -> new FormatProcessingInstruction());
    }
}
