package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;

/**
 * Правило для блочного элемента ДИТА - после головы ставим новую строку и отступ
 */
public class DitaNewLineAndIndentAfterBlockElementHead extends NodeFormat {
    public DitaNewLineAndIndentAfterBlockElementHead(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
        priority = Priority.OVERRIDE;
    }
//    public DitaNewLineAndIndentAfterBlockElementHead() {
//        super(/*AnotherNewLineAndIndentIfIndented.class*/);
//    }

    @Override
    public void doFormatting() {
        //новая строка только если последняя непуста
//        if (!xmlBuilder.isLastLineEmptyOrWhitespace()) {//todo remove
//            xmlBuilder.linefeed();
//        }
//        xmlBuilder.indent(ctx.indentLevel + 1);
    }
}
