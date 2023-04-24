package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;

/**
 * Правило для блочного элемента ДИТА - перед хвостом ставим новую строку и отступ
 */
public class DitaNewLineAndIndentBeforeBlockElementTail extends NodeFormat {
    public DitaNewLineAndIndentBeforeBlockElementTail(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
        priority = Priority.OVERRIDE;
    }
//    public DitaNewLineAndIndentBeforeBlockElementTail() {
//        super(FormatElementBeforeTail.class);
//    }

    @Override
    public void doFormatting() {
        //новая строка только если последняя непуста
        if (!xmlBuilder.isLastLineEmptyOrWhitespace()) {
            xmlBuilder.linefeed();
        }
        xmlBuilder.indent(ctx.indentLevel);
    }
}
