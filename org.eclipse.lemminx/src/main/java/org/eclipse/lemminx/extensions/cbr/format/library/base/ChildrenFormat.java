package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.FormatSequence;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;

import java.util.List;

/**
 * Частичное форматирование.
 * Применяет заданное форматирование для вывода всех дочерних узлов узла.
 * Если дочерних узлов нет, ничего не делает.
 */
public class ChildrenFormat extends Format {

    public ChildrenFormat(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        List<DOMNode> children = node.getChildren();
        children.forEach(ctx.formatSequence::doFormatting);
    }

}
