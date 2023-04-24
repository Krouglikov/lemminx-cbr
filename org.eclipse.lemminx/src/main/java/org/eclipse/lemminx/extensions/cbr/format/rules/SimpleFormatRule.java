package org.eclipse.lemminx.extensions.cbr.format.rules;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.FormatRule;

import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleFormatRule implements FormatRule {

    private final int kind;

    private final int sequence;

    private final Predicate<DOMNode> applicable;

    private final Function<DOMNode, ContextBoundFormat> operation;

    protected SimpleFormatRule(int kind, Predicate<DOMNode> applicable, Function<DOMNode,
            ContextBoundFormat> operation) {
        this.kind = kind;
        this.sequence = kind; //todo
        this.applicable = applicable;
        this.operation = operation;
    }

    protected SimpleFormatRule(FormattingOrder sequence, Predicate<DOMNode> applicable,
                               Function<DOMNode, ContextBoundFormat> operation) {
        this.kind = sequence.ordinal();
        this.sequence = sequence.ordinal();
        this.applicable = applicable;
        this.operation = operation;
    }

    @Override
    public int kind() {
        return kind;
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public boolean applicable(DOMNode node) {
        return applicable.test(node);
    }

    @Override
    public ContextBoundFormat apply(DOMNode node) {
        return operation.apply(node);
    }
}
