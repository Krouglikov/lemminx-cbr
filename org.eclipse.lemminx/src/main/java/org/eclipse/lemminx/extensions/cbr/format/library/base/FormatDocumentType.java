package org.eclipse.lemminx.extensions.cbr.format.library.base;

import org.eclipse.lemminx.dom.*;
import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.List;

import static org.eclipse.lemminx.extensions.cbr.format.library.base.FormatProlog.addPrologToXMLBuilder;

public class FormatDocumentType extends Format {

    public FormatDocumentType(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public void doFormatting() {
        formatDocumentType((DOMDocumentType) node, xmlBuilder);
    }

    /**
     * Format the given DOM document type.
     *
     * @param documentType the DOM document type to format.
     */
    private void formatDocumentType(DOMDocumentType documentType, XMLBuilder xmlBuilder) {
        boolean isDTD = documentType.getOwnerDocument().isDTD();
        if (!isDTD) {
            xmlBuilder.startDoctype();
            List<DTDDeclParameter> params = documentType.getParameters();

            for (DTDDeclParameter param : params) {
                if (!documentType.isInternalSubset(param)) {
                    xmlBuilder.addParameter(param.getParameter());
                } else {
                    xmlBuilder.startDoctypeInternalSubset();
                    xmlBuilder.linefeed();
                    // level + 1 since the 'level' value is the doctype tag's level
                    formatDTD(documentType, ctx.indentLevel + 1, ctx.endOffset, xmlBuilder);
                    xmlBuilder.linefeed();
                    xmlBuilder.endDoctypeInternalSubset();
                }
            }
            if (documentType.isClosed()) {
                xmlBuilder.endDoctype();
            }
            ctx.linefeedOnNextWrite = true;

        } else {
            formatDTD(documentType, ctx.indentLevel, ctx.endOffset, xmlBuilder);
        }
    }

    private static boolean formatDTD(DOMDocumentType doctype, int level, int end, XMLBuilder xmlBuilder) {
        DOMNode previous = null;
        for (DOMNode node : doctype.getChildren()) {
            if (previous != null) {
                xmlBuilder.linefeed();
            }

            xmlBuilder.indent(level);

            if (node.isText()) {
                xmlBuilder.addContent(((DOMText) node).getData().trim());
            } else if (node.isComment()) {
                DOMComment comment = (DOMComment) node;
                xmlBuilder.startComment(comment);
                xmlBuilder.addContentComment(comment.getData());
                xmlBuilder.endComment();
            } else if (node.isProcessingInstruction()) {
                addPIToXMLBuilder(node, xmlBuilder);
            } else if (node.isProlog()) {
                addPrologToXMLBuilder(node, xmlBuilder);
            } else {
                boolean setEndBracketOnNewLine = false;
                DTDDeclNode decl = (DTDDeclNode) node;
                xmlBuilder.addDeclTagStart(decl);

                if (decl.isDTDAttListDecl()) {
                    DTDAttlistDecl attlist = (DTDAttlistDecl) decl;
                    List<DTDAttlistDecl> internalDecls = attlist.getInternalChildren();

                    if (internalDecls == null) {
                        for (DTDDeclParameter param : decl.getParameters()) {
                            xmlBuilder.addParameter(param.getParameter());
                        }
                    } else {
                        boolean multipleInternalAttlistDecls = false;
                        List<DTDDeclParameter> params = attlist.getParameters();
                        DTDDeclParameter param;
                        for (int i = 0; i < params.size(); i++) {
                            param = params.get(i);
                            if (attlist.getNameParameter().equals(param)) {
                                xmlBuilder.addParameter(param.getParameter());
                                if (attlist.getParameters().size() > 1) { // has parameters after elementName
                                    xmlBuilder.linefeed();
                                    xmlBuilder.indent(level + 1);
                                    setEndBracketOnNewLine = true;
                                    multipleInternalAttlistDecls = true;
                                }
                            } else {
                                if (multipleInternalAttlistDecls && i == 1) {
                                    xmlBuilder.addUnindentedParameter(param.getParameter());
                                } else {
                                    xmlBuilder.addParameter(param.getParameter());
                                }
                            }
                        }

                        for (DTDAttlistDecl attlistDecl : internalDecls) {
                            xmlBuilder.linefeed();
                            xmlBuilder.indent(level + 1);
                            params = attlistDecl.getParameters();
                            for (int i = 0; i < params.size(); i++) {
                                param = params.get(i);

                                if (i == 0) {
                                    xmlBuilder.addUnindentedParameter(param.getParameter());
                                } else {
                                    xmlBuilder.addParameter(param.getParameter());
                                }
                            }
                        }
                    }
                } else {
                    for (DTDDeclParameter param : decl.getParameters()) {
                        xmlBuilder.addParameter(param.getParameter());
                    }
                }
                if (setEndBracketOnNewLine) {
                    xmlBuilder.linefeed();
                    xmlBuilder.indent(level);
                }
                if (decl.isClosed()) {
                    xmlBuilder.closeStartElement();
                }
            }
            previous = node;
        }
        return true;
    }

    //todo check for duplication
    private static void addPIToXMLBuilder(DOMNode node, XMLBuilder xml) {
        DOMProcessingInstruction processingInstruction = (DOMProcessingInstruction) node;
        xml.startPrologOrPI(processingInstruction.getTarget());

        String content = processingInstruction.getData();
        if (content.length() > 0) {
            xml.addContentPI(content);
        } else {
            xml.addContent(" ");
        }

        xml.endPrologOrPI();
    }

}
