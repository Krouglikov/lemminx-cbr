package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormatConfiguration;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;

import java.util.List;

/**
 * Частичное форматирование.
 * Применяет заданное форматирование для вывода всех дочерних узлов узла.
 * Если дочерних узлов нет, ничего не делает.
 */
public class ChildrenFormat extends NodeFormat {

    public ChildrenFormat(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        NodeFormatConfiguration nodeFormatConfiguration = ctx.nodeFormatConfiguration;
        List<DOMNode> children = node.getChildren();
        children.forEach(child -> {
                    nodeFormatConfiguration.getSequenceFormatForNode(child)
                            .doFormatting();
                }
        );
    }

}
