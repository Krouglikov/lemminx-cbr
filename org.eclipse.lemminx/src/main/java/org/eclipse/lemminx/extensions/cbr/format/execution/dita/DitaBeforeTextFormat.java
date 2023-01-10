package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.OverrideFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.AnotherNewLineAndIndentIfIndented;
import org.eclipse.lemminx.utils.XMLBuilder;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isDitaBlockElement;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isEmptyOrWhitespaceOnlyText;

public class DitaBeforeTextFormat extends OverrideFormat {

    public DitaBeforeTextFormat() {
        super(AnotherNewLineAndIndentIfIndented.class);
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        formatBeforeHead(domNode, xmlBuilder);
    }

    private void formatBeforeHead(DOMNode textNode, XMLBuilder xmlBuilder) {
        boolean emptyOrWhitespaceOnly = isEmptyOrWhitespaceOnlyText().test(textNode);
        DOMNode parentNode = textNode.getParentNode();
        DOMNode previousSibling = textNode.getPreviousSibling();
        boolean firstChild = parentNode.getFirstChild() == textNode;
        if (firstChild) {
            // если это первый в блочном элементе фрагмент текста,
            // его следует отделить переносом строки, но только если он сам не перенос строки
            if (isDitaBlockElement().test(parentNode)) {
                if (!emptyOrWhitespaceOnly) {
                    xmlBuilder.linefeed();
                    xmlBuilder.indent(ctx.indentLevel);
                }
            }
        } else {
            if (isDitaBlockElement().test(previousSibling)) {
                // текст от блочного элемента отделяем переносом строки
                if (!emptyOrWhitespaceOnly) {
                    xmlBuilder.linefeed();
                    xmlBuilder.indent(ctx.indentLevel);
                }
            }
        }
    }

}
