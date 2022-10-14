package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.OverrideFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatElementBeforeTail;
import org.eclipse.lemminx.utils.XMLBuilder;

/**
 * Правило для блочного элемента ДИТА - перед хвостом ставим новую строку и отступ
 */
public class NewLineAndIndentBeforeTail extends OverrideFormat {
    public NewLineAndIndentBeforeTail() {
        super(FormatElementBeforeTail.class);
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        //новая строка только если последняя непуста
        if (!xmlBuilder.isLastLineEmptyOrWhitespace()) {
            xmlBuilder.linefeed();
        }
        xmlBuilder.indent(ctx.indentLevel);
    }
}
