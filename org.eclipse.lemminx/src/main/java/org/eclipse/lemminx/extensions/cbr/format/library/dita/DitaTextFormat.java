package org.eclipse.lemminx.extensions.cbr.format.library.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.extensions.cbr.utils.LineSeeker;
import org.eclipse.lemminx.extensions.cbr.CbrXMLFormatterDocument;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.utils.LineWriter;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

public class DitaTextFormat extends Format {
    public DitaTextFormat(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
        priority = Priority.OVERRIDE;
    }

//    public DitaTextFormat() {
//        super(FormatText.class);
//    }

    @Override
    public void doFormatting() {
        formatAsText(node, xmlBuilder);
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
                .maxLineLength(CbrXMLFormatterDocument.getMaxLineLength())
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
