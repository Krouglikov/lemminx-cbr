package org.eclipse.lemminx.extensions.cbr.format.rules.children;

import org.eclipse.lemminx.extensions.cbr.format.execution.base.ChildrenFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Если у узла есть дочерние, их нужно выводить.
 */
public class PrintChildrenIfExistRule extends SimpleFormatRule {
    public PrintChildrenIfExistRule() {
        super(FormattingOrder.CHILDREN,
                node -> node.hasChildNodes() && !node.isDoctype(), //todo children of Doctype as formatter
                node -> new ChildrenFormat());
    }
}
