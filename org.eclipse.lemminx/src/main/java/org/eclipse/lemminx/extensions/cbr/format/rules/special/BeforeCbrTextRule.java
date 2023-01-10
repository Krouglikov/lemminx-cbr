package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaBeforeTextFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.hasDitaBlockAncestor;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isText;

/**
 * Форматирование текста внутри блочного элемента ДИТА (связки между соседними элементами)
 */
public class BeforeCbrTextRule extends SimpleFormatRule {
    public BeforeCbrTextRule() {
        super(FormattingSequence.BEFORE_HEAD,
                isText().and(hasDitaBlockAncestor()),
                node -> new DitaBeforeTextFormat());
    }
}
