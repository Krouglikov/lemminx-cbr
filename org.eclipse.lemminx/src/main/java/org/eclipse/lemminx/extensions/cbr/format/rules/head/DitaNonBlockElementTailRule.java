package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaContentsTailFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.*;

/**
 * Особое правило для хвоста элементов в контексте ДИТА (внутри блочного элемента ДИТА)
 * кроме комментариев, текста, не являющихся блочными элементами ДИТА
 */
public class DitaNonBlockElementTailRule extends SimpleFormatRule {
    public DitaNonBlockElementTailRule() {
        super(FormattingOrder.TAIL,
                isNotOneLineComment() // todo remove?
                        .and(isNotComment()) //todo duplicate code
                        .and(isNotText())
                        .and(isNotDitaBlockElement())
                        .and(hasDitaBlockAncestor()),
                node -> new DitaContentsTailFormat());
    }
}
