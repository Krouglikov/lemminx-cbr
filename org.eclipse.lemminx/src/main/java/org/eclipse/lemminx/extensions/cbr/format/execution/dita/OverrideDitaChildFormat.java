package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.LineSeeker;
import org.eclipse.lemminx.extensions.cbr.XmlFormatterService;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.LineWriter;
import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.OverrideFormat;
import org.eclipse.lemminx.utils.XMLBuilder;

public abstract class OverrideDitaChildFormat extends OverrideFormat {
    public OverrideDitaChildFormat(Class<? extends Format>... overrides) {
        super(overrides);
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        formatAsText(domNode, xmlBuilder);
    }

    protected abstract String getContent(DOMNode node, Context ctx);

    protected abstract String getDelimiter(DOMNode node);

    public void formatAsText(
            DOMNode textNode,
            XMLBuilder xmlBuilder) {
        DOMNode parentNode = textNode.getParentNode();
        boolean firstChild = parentNode.getFirstChild() == textNode;
        String content = getContent(textNode, ctx);
        int indentLevel = ctx.indentLevel;
        int blanks = indentLevel * ctx.sharedSettings.getFormattingSettings().getTabSize();

        if (firstChild) { //todo move to another formatter
            xmlBuilder.linefeed();
            xmlBuilder.indent(indentLevel);
        } else if (textNode.getPreviousSibling().isText()) {
            xmlBuilder.addContent(" ");
        } else if (Predicates.isDitaBlockElement().test(textNode.getPreviousSibling())) {
            xmlBuilder.linefeed();
            xmlBuilder.indent(indentLevel);
        } else {
            xmlBuilder.addContent(" ");
        }
        LineWriter lineWriter = new LineWriter(xmlBuilder, indentLevel);
        String delimiter = getDelimiter(textNode);
        int startPosition = xmlBuilder.lastLineLength();
        LineSeeker.setup()
                .lineBreak(delimiter)
                .maxLineLength(XmlFormatterService.getMaxLineLength())
                .firstLineOffset(startPosition)
                .otherLinesOffset(blanks)
                .onNewLine(lineWriter)
                .run(content);
    }

}
