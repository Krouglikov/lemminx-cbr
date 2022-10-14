package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.FormatConfiguration;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.List;

/**
 * Частичное форматирование.
 * Применяет заданное форматирование для вывода всех дочерних узлов узла.
 * Если дочерних узлов нет, ничего не делает.
 */
public class ChildrenFormat extends ContextBoundFormat {

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        FormatConfiguration formatConfiguration = ctx.formatConfiguration;
        List<DOMNode> children = domNode.getChildren();
        children.forEach(child -> formatConfiguration
                .configure(child)
                .withContext(ctx)
                .accept(child, xmlBuilder));
    }

}
