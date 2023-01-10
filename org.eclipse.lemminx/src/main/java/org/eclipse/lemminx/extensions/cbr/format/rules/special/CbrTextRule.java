package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaTextFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.hasDitaBlockAncestor;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isText;

/**
 * Форматирование текста внутри блочного элемента ДИТА по особым правилам (с учетом длины строки)
 */
public class CbrTextRule extends SimpleFormatRule {
    public CbrTextRule() {
        super(FormattingSequence.HEAD,
                isText().and(hasDitaBlockAncestor()),
                node -> new DitaTextFormat());
    }
}
