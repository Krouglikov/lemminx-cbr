package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.extensions.cbr.format.execution.base.AnotherNewLineAndIndentIfIndented;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isNotDocumentNode;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isTypicalAsChild;

/**
 * Перед головой элемента вставляется дополнительный перенос строки и отступ если узел должен иметь отступ
 */
public class AnotherNewLineAndIndentBeforHeadRule extends SimpleFormatRule {
    public AnotherNewLineAndIndentBeforHeadRule() {
        super(FormattingSequence.BEFORE_HEAD,
                isNotDocumentNode().and(isTypicalAsChild()),
                node -> new AnotherNewLineAndIndentIfIndented()
        );
    }
}
