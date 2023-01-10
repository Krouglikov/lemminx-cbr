package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.OverrideFormat;
import org.eclipse.lemminx.utils.XMLBuilder;

/**
 * Правило для блочного элемента ДИТА - после головы ставим новую строку и отступ
 */
public class DitaNewLineAndIndentAfterBlockElementHead extends OverrideFormat {
    public DitaNewLineAndIndentAfterBlockElementHead() {
        super(/*AnotherNewLineAndIndentIfIndented.class*/);
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        //новая строка только если последняя непуста
//        if (!xmlBuilder.isLastLineEmptyOrWhitespace()) {//todo remove
//            xmlBuilder.linefeed();
//        }
//        xmlBuilder.indent(ctx.indentLevel + 1);
    }
}
