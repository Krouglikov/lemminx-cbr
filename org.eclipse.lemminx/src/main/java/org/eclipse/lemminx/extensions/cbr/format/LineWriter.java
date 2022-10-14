package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.function.Consumer;

public class LineWriter implements Consumer<String> {
    final XMLBuilder xmlBuilder;
    final int indentLevel;

    int count = 0;

    public LineWriter(XMLBuilder xmlBuilder, int indentLevel) {
        this.indentLevel = indentLevel;
        this.xmlBuilder = xmlBuilder;
    }

    @Override
    public void accept(String line) {
        if (count > 0) {
            xmlBuilder.linefeed();
            if (!line.isEmpty()) {
                xmlBuilder.indent(indentLevel);
            }
        }
        xmlBuilder.addContent(line);
        count++;
    }
}
