package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.extensions.cbr.LineSeeker;
import org.eclipse.lemminx.extensions.cbr.XmlFormatterService;
import org.eclipse.lemminx.extensions.cbr.format.LineWriter;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.OverrideFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.FormatText;
import org.eclipse.lemminx.utils.XMLBuilder;

public class DitaTextFormat extends OverrideFormat {

    public DitaTextFormat() {
        super(FormatText.class);
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        formatAsText(domNode, xmlBuilder);
    }


    public void formatAsText(
            DOMNode textNode,
            XMLBuilder xmlBuilder) {
        String content = getContent(textNode, ctx);
        int indentLevel = ctx.indentLevel;
        int blanks = indentLevel * ctx.sharedSettings.getFormattingSettings().getTabSize();
        LineWriter lineWriter = new LineWriter(xmlBuilder, indentLevel);
        String delimiter = getDelimiter(textNode);
        int startPosition = xmlBuilder.lastLineLength();
        boolean noSpaceBeforeText = !xmlBuilder.isLastPositionWthitespace();
        LineSeeker.setup()
                .lineBreak(delimiter)
                .maxLineLength(XmlFormatterService.getMaxLineLength())
                .firstLineOffset(startPosition)
                .preserveLeadingSpace(noSpaceBeforeText)
                .otherLinesOffset(blanks)
                .onNewLine(lineWriter)
                .run(content);
    }

    protected String getContent(DOMNode node, Context ctx) {
        return ((DOMText) node).getData();
    }

    protected String getDelimiter(DOMNode node) {
        return ((DOMText) node).getDelimiter();
    }

}
