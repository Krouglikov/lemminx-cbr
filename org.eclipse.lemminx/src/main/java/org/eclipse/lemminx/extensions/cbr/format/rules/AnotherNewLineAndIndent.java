package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.AnotherNewLineAndIndentIfContextAllows;

public class AnotherNewLineAndIndent extends SimpleFormatRule {
    public AnotherNewLineAndIndent() {
        super(RuleSequence.BEFORE_HEAD,
                Predicates.isNotDocumentNode().and(Predicates.needAnotherLineFeed()),
                node -> new AnotherNewLineAndIndentIfContextAllows()
        );
    }
}
