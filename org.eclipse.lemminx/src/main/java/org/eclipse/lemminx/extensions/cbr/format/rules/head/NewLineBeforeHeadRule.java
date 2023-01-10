package org.eclipse.lemminx.extensions.cbr.format.rules.head;

import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.NewLineIfContextDemands;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingSequence;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Новая строка должна вставляться перед не-текстовым и непустым текстовым элементом
 */
public class NewLineBeforeHeadRule extends SimpleFormatRule {
    public NewLineBeforeHeadRule() {
        super(FormattingSequence.BEFORE_HEAD,
                Predicates.isNotText().or(Predicates.isNotEmptyText()),
                node -> new NewLineIfContextDemands()
        );
    }
}
