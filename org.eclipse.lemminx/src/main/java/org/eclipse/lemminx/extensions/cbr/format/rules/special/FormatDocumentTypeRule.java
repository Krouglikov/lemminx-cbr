package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatDocumentType;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Узлы DOCTYPE форматируются по своим собственным правилам.
 */
public class FormatDocumentTypeRule extends SimpleFormatRule {
    public FormatDocumentTypeRule() {
        super(1,
                DOMNode::isDoctype,
                node -> new FormatDocumentType());
    }
}
