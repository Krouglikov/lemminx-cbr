package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;

import java.util.List;

/**
 * Комбинация нескольких форматов.
 * Форматы применяются к узлу один за другим.
 */
public class NodeFormatSequence {

    private final List<NodeFormat> formats;

//    public FormatSequence(Format... sequence) {
//        if (sequence == null) {
//            throw new IllegalArgumentException();
//        }
//        this.sequence = Arrays.asList(sequence);
//    }

    public NodeFormatSequence(List<NodeFormat> formats) {
        this.formats = formats;
    }

    //    @Override
    public void doFormatting() {
        formats.forEach(NodeFormat::doFormatting);
    }
}
