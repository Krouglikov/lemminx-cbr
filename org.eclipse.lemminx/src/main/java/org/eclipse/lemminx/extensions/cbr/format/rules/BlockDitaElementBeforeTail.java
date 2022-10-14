package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.extensions.cbr.format.execution.dita.NewLineAndIndentBeforeTail;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.isDitaBlockElement;

public class BlockDitaElementBeforeTail extends SimpleFormatRule {
    public BlockDitaElementBeforeTail() {
        super(RuleSequence.BEFORE_TAIL,
                isDitaBlockElement(),
                node -> new NewLineAndIndentBeforeTail());
    }
}
