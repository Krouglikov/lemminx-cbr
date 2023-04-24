package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.extensions.cbr.format.execution.base.AnotherNewLineAndIndentIfIndented;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isNotDocumentNode;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isTypicalAsChild;

/**
 * Перед головой элемента вставляется дополнительный перенос строки и отступ если узел должен иметь отступ
 */
public class AnotherNewLineAndIndentBeforeHeadRule extends SimpleFormatRule {
    public AnotherNewLineAndIndentBeforeHeadRule() {
        super(FormattingOrder.BEFORE_HEAD,
                isNotDocumentNode().and(isTypicalAsChild()),
                node -> new AnotherNewLineAndIndentIfIndented()
        );
    }
}
