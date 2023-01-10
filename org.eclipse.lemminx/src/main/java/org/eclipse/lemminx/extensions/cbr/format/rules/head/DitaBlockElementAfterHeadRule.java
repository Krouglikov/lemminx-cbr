package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaNewLineAndIndentAfterBlockElementHead;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isDitaBlockElement;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isNotSelfClosed;

/**
 * Формат после головы элемента ДИТА
 */
public class DitaBlockElementAfterHeadRule extends SimpleFormatRule {
    public DitaBlockElementAfterHeadRule() {
        super(FormattingSequence.AFTER_HEAD,
                isDitaBlockElement().and(isNotSelfClosed()),
                node -> new DitaNewLineAndIndentAfterBlockElementHead());
    }
}
