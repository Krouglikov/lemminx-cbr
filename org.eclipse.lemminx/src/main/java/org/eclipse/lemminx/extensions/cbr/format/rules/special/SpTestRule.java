package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatText;
import org.eclipse.lemminx.extensions.cbr.format.execution.dita.SpTestFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

public class SpTestRule extends SimpleFormatRule {
    public SpTestRule() {
        super(0,
                Predicates.isText(),
                node -> new SpTestFormat());
    }
}
