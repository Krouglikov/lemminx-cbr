package org.eclipse.lemminx.extensions.cbr.format.library.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

import static org.eclipse.lemminx.extensions.cbr.format.library.Predicates.isDitaBlockElement;
import static org.eclipse.lemminx.extensions.cbr.format.library.Predicates.isEmptyOrWhitespaceOnlyText;

public class DitaBeforeTextFormat extends Format {
    public DitaBeforeTextFormat(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

//    public DitaBeforeTextFormat() {
//        super(AnotherNewLineAndIndentIfIndented.class);
//    }

    @Override
    public void doFormatting() {
        formatBeforeHead(node, xmlBuilder);
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
