package org.eclipse.lemminx.extensions.cbr.format.rules.tail;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaNewLineAndIndentBeforeBlockElementTail;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isDitaBlockElement;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isNotSelfClosed;

/**
 * Перед хвостом блочного элемента ДИТА (кроме самозакрывающихся) -- перенос строки и отступ
 */
public class DitaBlockElementBeforeTailRule extends SimpleFormatRule {
    public DitaBlockElementBeforeTailRule() {
        super(FormattingSequence.BEFORE_TAIL,
                isDitaBlockElement().and(isNotSelfClosed()),
                node -> new DitaNewLineAndIndentBeforeBlockElementTail());
    }
}
