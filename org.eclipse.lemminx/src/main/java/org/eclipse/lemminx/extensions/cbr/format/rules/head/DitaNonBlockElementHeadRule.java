package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaContentsHeadFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.*;

/**
 * Особое правило для головы элементов в контексте ДИТА (внутри блочного элемента ДИТА)
 * кроме комментариев, текста, не являющихся блочными элементами ДИТА
 */
public class DitaNonBlockElementHeadRule extends SimpleFormatRule {
    public DitaNonBlockElementHeadRule() {
        super(FormattingSequence.HEAD,
                isNotOneLineComment() // todo remove?
                        .and(isNotComment())
                        .and(isNotText())
                        .and(isNotDitaBlockElement())
                        .and(hasDitaBlockAncestor()),
                node -> new DitaContentsHeadFormat());
    }
}
