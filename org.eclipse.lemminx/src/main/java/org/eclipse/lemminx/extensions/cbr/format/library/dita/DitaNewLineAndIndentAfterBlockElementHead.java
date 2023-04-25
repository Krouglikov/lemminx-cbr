package org.eclipse.lemminx.extensions.cbr.format.library.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;

/**
 * Правило для блочного элемента ДИТА - после головы ставим новую строку и отступ
 */
public class DitaNewLineAndIndentAfterBlockElementHead extends Format {
    public DitaNewLineAndIndentAfterBlockElementHead(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
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
