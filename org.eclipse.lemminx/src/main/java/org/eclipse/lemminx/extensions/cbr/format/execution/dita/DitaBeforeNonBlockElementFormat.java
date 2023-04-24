package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isDitaBlockElement;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isEmptyOrWhitespaceOnlyText;

/**
 * Форматирование неблочного элемента внутри блочного (отделение элементов друг от друга)
 */
public class DitaBeforeNonBlockElementFormat extends NodeFormat {
    public DitaBeforeNonBlockElementFormat(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
        priority = Priority.OVERRIDE;
    }

//    @Override
//    public Stream<Class<? extends Format>> overrides() {
//        return Stream.of(
//                NewLineIfContextDemands.class,
//                AnotherNewLineAndIndentIfIndented.class);
//    }

    @Override
    public void doFormatting() {
        formatBeforeHead(node, xmlBuilder);
    }


    private void formatBeforeHead(DOMNode node, XMLBuilder xmlBuilder) {
        DOMNode parentNode = node.getParentNode();
        DOMNode previousSibling = node.getPreviousSibling();

        // если прямо перед нами текстовый узел с голым форматированием, игнорируем его
        if (isEmptyOrWhitespaceOnlyText().test(previousSibling)) {
            previousSibling = previousSibling.getPreviousSibling();
        }

        boolean firstChild = parentNode.getFirstChild() == node;
        if (firstChild || previousSibling == null) {
            // если это первый в блочном элементе фрагмент текста,
            // его следует отделить переносом строки, но только если он сам не перенос строки
            if (isDitaBlockElement().test(parentNode)) {
                xmlBuilder.linefeed();
                xmlBuilder.indent(ctx.indentLevel);
            }
        } else {
            if (isDitaBlockElement().test(previousSibling)) {
                // неблочный элемент от блочного элемента отделяем переносом строки
                xmlBuilder.linefeed();
                xmlBuilder.indent(ctx.indentLevel);
            }
        }
    }

}
