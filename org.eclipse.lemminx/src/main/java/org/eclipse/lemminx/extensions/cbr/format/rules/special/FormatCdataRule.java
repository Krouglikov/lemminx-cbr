package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatCData;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Узлы CDATA форматируются по собственным правилам
 */
public class FormatCdataRule extends SimpleFormatRule {
    public FormatCdataRule() {
        super(1,
                DOMNode::isCDATA,
                node -> new FormatCData());
    }
}
