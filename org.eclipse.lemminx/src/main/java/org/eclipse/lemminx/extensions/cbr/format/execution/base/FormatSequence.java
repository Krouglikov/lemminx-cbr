package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;

import java.util.Arrays;
import java.util.List;

/**
 * Комбинация нескольких форматов.
 * Форматы применяются к узлу один за другим.
 */
public class FormatSequence {

    private final List<ContextBoundFormat> sequence;

    private DOMNode node;

    private Context ctx;

//    public FormatSequence(Format... sequence) {
//        if (sequence == null) {
//            throw new IllegalArgumentException();
//        }
//        this.sequence = Arrays.asList(sequence);
//    }

    public FormatSequence(DOMNode node, Context ctx, List<ContextBoundFormat> formats) {
        this.node = node;
        this.ctx = ctx;
        sequence = formats;
    }

    //    @Override
    public void doFormatting() {
        sequence.forEach(format -> {
            format.withContext(ctx);
            format.doFormatting(node);
        });
    }
}
