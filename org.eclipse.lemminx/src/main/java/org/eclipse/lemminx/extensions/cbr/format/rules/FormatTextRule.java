package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatText;

public class FormatTextRule extends SimpleFormatRule {
    public FormatTextRule() {
        super(1,
                Predicates.isText(),
                node -> new FormatText());
    }
}
