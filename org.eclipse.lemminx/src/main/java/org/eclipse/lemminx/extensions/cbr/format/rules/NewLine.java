package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.NewLineIfContextDemands;

/**
 * Новая строка должна вставляться перед не-текстовым и непустым текстовым элементом
 */
public class NewLine extends SimpleFormatRule {
    public NewLine() {
        super(RuleSequence.BEFORE_HEAD,
                Predicates.isNotText().or(Predicates.isNotEmptyText()),
                node -> new NewLineIfContextDemands()
        );
    }
}
