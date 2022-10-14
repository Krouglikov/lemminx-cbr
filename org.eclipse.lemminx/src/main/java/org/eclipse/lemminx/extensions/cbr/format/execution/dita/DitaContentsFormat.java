package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.FormatConfiguration;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.*;
import org.eclipse.lemminx.utils.XMLBuilder;

import static java.lang.System.lineSeparator;

public class DitaContentsFormat extends OverrideDitaChildFormat {

    public DitaContentsFormat() {
        super(AnotherNewLineAndIndentIfContextAllows.class,
                FormatElementHead.class,
                IncreaseIndent.class, ChildrenFormat.class, DecreaseIndent.class,
                FormatElementTail.class
        );
    }

    @Override
    protected String getContent(DOMNode node, Context ctx) {
        return complexPrintout(node, ctx);
    }


    @Override
    public String getDelimiter(DOMNode node) {
        try {
            return node.getOwnerDocument().getTextDocument().lineDelimiter(0);
        } catch (BadLocationException e) {
            return lineSeparator();
        }
    }

    private String complexPrintout(DOMNode node, Context ctx) { //tod bullshit!
        XMLBuilder xmlBuilder = ctx.newXmlBuilder(getDelimiter(node));
        //Context context = new Context();
        FormatConfiguration.lemminx()
                .configure(node)
                .withContext(ctx)
                .accept(node, xmlBuilder);
        String s = xmlBuilder.toString();
        return s;
    }

    private String simplePrintout(DOMNode node, Context ctx) {
        XMLBuilder xmlBuilder = ctx.newXmlBuilder(getDelimiter(node));

        xmlBuilder.startElement(node.getNodeName(), true);
        if (node.hasChildNodes()) {
            node.getChildren().forEach(child ->
                    xmlBuilder.addContent(child.getTextContent()));
        }
        xmlBuilder.endElement(node.getNodeName(), true);

        return xmlBuilder.toString();
    }

}
