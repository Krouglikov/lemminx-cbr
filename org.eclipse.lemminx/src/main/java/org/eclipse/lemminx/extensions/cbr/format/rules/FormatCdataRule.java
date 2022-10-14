package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatCData;

public class FormatCdataRule extends SimpleFormatRule {
    public FormatCdataRule() {
        super(1,
                DOMNode::isCDATA,
                node -> new FormatCData());
    }
}
