package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaBeforeNonBlockElementFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.*;

/**
 * Форматирование текста внутри блочного элемента ДИТА (связки между соседними элементами)
 */
public class DitaBeforeNonBlockElementRule extends SimpleFormatRule {
    public DitaBeforeNonBlockElementRule() {
        super(FormattingOrder.BEFORE_HEAD,
                isNotOneLineComment() // todo remove?
                        .and(isNotComment())
                        .and(isNotText())
                        .and(isNotDitaBlockElement())
                        .and(hasDitaBlockAncestor()),
                node -> new DitaBeforeNonBlockElementFormat());
    }
}
