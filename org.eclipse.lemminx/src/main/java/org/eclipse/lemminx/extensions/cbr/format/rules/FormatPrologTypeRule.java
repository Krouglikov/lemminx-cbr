package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatProlog;

public class FormatPrologTypeRule extends SimpleFormatRule {
    public FormatPrologTypeRule() {
        super(1,
                DOMNode::isProlog,
                node -> new FormatProlog());
    }
}
