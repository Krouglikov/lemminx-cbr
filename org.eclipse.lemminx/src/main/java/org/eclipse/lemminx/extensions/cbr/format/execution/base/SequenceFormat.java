package org.eclipse.lemminx.extensions.cbr.format.execution.base;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Комбинация нескольких форматов.
 * Форматы применяются к узлу один за другим.
 */
public class SequenceFormat extends ContextBoundFormat {

    private final List<Format> sequence;

    public SequenceFormat(Format... sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException();
        }
        this.sequence = Arrays.asList(sequence);
    }

    public SequenceFormat(List<Format> formats) {
        sequence = formats;
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        sequence.forEach(format -> format
                .withContext(ctx)
                .accept(domNode, xmlBuilder));
    }
}
