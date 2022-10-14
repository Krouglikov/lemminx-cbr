package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaContentsFormat;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.*;

public class DitaNonBlockElements extends SimpleFormatRule {
    public DitaNonBlockElements() {
        super(RuleSequence.HEAD,
                isNotOneLineComment()
                        .and(isNotText())
                        .and(isNotComment())
                        .and(isNotDitaBlockElement())
                        .and(hasDitaBlockParent()),
                node -> new DitaContentsFormat());
    }
}
