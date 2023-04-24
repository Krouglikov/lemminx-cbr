package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;

import java.util.Arrays;
import java.util.List;

/**
 * Комбинация нескольких форматов.
 * Форматы применяются к узлу один за другим.
 */
public class SequenceFormat {

    private final List<Format> sequence;

    private DOMNode node;

    private Context ctx;

    public SequenceFormat(Format... sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException();
        }
        this.sequence = Arrays.asList(sequence);
    }

    public SequenceFormat(DOMNode node, Context ctx, List<Format> formats) {
        this.node = node;
        this.ctx = ctx;
        sequence = formats;
    }

    //    @Override
    public void doFormatting() {
        sequence.forEach(format -> {
            format
                    .withContext(ctx)
                    .accept(node, ctx.xmlBuilder);
        });
    }
}
