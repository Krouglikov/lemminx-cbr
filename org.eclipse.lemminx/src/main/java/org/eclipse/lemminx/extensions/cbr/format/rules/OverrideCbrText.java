package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.OverrideTextFormat;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.hasDitaBlockParent;
import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isText;

public class OverrideCbrText extends SimpleFormatRule {
    public OverrideCbrText() {
        super(RuleSequence.HEAD,
                isText().and(hasDitaBlockParent()),
                node -> new OverrideTextFormat());
    }
}
