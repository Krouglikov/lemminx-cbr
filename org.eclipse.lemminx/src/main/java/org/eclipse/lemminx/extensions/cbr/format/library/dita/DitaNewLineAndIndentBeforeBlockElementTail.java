package org.eclipse.lemminx.extensions.cbr.format.library.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;

/**
 * Правило для блочного элемента ДИТА - перед хвостом ставим новую строку и отступ
 */
public class DitaNewLineAndIndentBeforeBlockElementTail extends Format {
    public DitaNewLineAndIndentBeforeBlockElementTail(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
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
