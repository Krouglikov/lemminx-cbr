package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.CbrXMLFormatterDocument;
import org.eclipse.lemminx.extensions.cbr.LineSeeker;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.LineWriter;
import org.eclipse.lemminx.extensions.cbr.format.execution.OverrideFormat;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.*;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.stream.Stream;

import static java.lang.System.lineSeparator;

public class DitaContentsTailFormat extends OverrideFormat {

    @Override
    public Stream<Class<? extends Format>> overrides() {
        return Stream.of(
                NewLineIfContextDemands.class,
                AnotherNewLineAndIndentIfIndented.class,
                FormatElementHead.class, //ChildrenFormat.class,
                FormatElementBeforeTail.class, FormatElementTail.class);
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        XMLBuilder xmlBuilder1 = new XMLBuilder(xmlBuilder.getSharedSettings(), "", "");
        new FormatElementTail().withContext((Object) ctx).accept(domNode, xmlBuilder1);
        String content = xmlBuilder1.toString();

        int indentLevel = ctx.indentLevel;
        int blanks = indentLevel * ctx.sharedSettings.getFormattingSettings().getTabSize();
        LineWriter lineWriter = new LineWriter(xmlBuilder, indentLevel);
        String delimiter = getDelimiter(domNode);
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
