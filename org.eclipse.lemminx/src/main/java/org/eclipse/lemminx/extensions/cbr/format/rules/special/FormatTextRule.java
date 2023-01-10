package org.eclipse.lemminx.extensions.cbr.format.rules.special;

import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatText;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;

/**
 * Форматирование текста
 */
public class FormatTextRule extends SimpleFormatRule {
    public FormatTextRule() {
        super(1,
                Predicates.isText(),
                node -> new FormatText());
    }
}
