package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatProlog;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Пролог форматируется по собственным правилам
 */
public class FormatPrologTypeRule extends SimpleFormatRule {
    public FormatPrologTypeRule() {
        super(1,
                DOMNode::isProlog,
                node -> new FormatProlog());
    }
}
