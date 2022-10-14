package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.extensions.cbr.format.execution.base.ChildrenFormat;

/**
 * Если у узла есть дочерние, их нужно выводить.
 */
public class PrintChildrenIfExist extends SimpleFormatRule {
    public PrintChildrenIfExist() {
        super(RuleSequence.CHILDREN,
                node -> node.hasChildNodes() && !node.isDoctype(), //todo children of Doctype as formatter
                node -> new ChildrenFormat());
    }
}
