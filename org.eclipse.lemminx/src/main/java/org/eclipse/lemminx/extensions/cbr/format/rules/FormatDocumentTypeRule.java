package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatDocumentType;

public class FormatDocumentTypeRule extends SimpleFormatRule {
    public FormatDocumentTypeRule() {
        super(1,
                DOMNode::isDoctype,
                node -> new FormatDocumentType());
    }
}
