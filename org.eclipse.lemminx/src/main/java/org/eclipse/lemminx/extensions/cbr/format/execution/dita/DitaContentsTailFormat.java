package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.CbrXMLFormatterDocument;
import org.eclipse.lemminx.extensions.cbr.utils.LineSeeker;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.LineWriter;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.*;
import org.eclipse.lemminx.utils.XMLBuilder;

import static java.lang.System.lineSeparator;

public class DitaContentsTailFormat extends NodeFormat {
    public DitaContentsTailFormat(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
        priority = Priority.OVERRIDE;
    }

//    @Override
//    public Stream<Class<? extends Format>> overrides() {
//        return Stream.of(
//                NewLineIfContextDemands.class,
//                AnotherNewLineAndIndentIfIndented.class,
//                FormatElementHead.class, //ChildrenFormat.class,
//                FormatElementBeforeTail.class, FormatElementTail.class);
//    }

    @Override
    public void doFormatting() {
        XMLBuilder xmlBuilder1 = new XMLBuilder(xmlBuilder.getSharedSettings(), "", "");
        FormatElementTail formatElementTail = new FormatElementTail(node, ctx, FormattingOrder.TAIL);
        formatElementTail.setXmlBuilder(xmlBuilder1);
        formatElementTail.doFormatting();
        String content = xmlBuilder1.toString();

        int indentLevel = ctx.indentLevel;
        int blanks = indentLevel * ctx.sharedSettings.getFormattingSettings().getTabSize();
        LineWriter lineWriter = new LineWriter(xmlBuilder, indentLevel);
        String delimiter = getDelimiter(node);
        int startPosition = xmlBuilder.lastLineLength();

        LineSeeker.setup()
                .lineBreak(delimiter)
                .maxLineLength(CbrXMLFormatterDocument.getMaxLineLength())
                .firstLineOffset(startPosition)
                .otherLinesOffset(blanks)
                .onNewLine(lineWriter)
                .run(content);
    }

    public String getDelimiter(DOMNode node) { //todo вынести в утилиты
        try {
            return node.getOwnerDocument().getTextDocument().lineDelimiter(0);
        } catch (BadLocationException e) {
            return lineSeparator();
        }
    }
}
