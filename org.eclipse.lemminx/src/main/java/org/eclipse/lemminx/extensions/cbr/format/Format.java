package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

public abstract class Format {
    private FormattingOrder order = FormattingOrder.UNDEFINED;
    protected DOMNode node;
    protected Context ctx;
    protected XMLBuilder xmlBuilder;

    private Format() {
    }

    public Format(DOMNode node, Context ctx, FormattingOrder order) {
        this.node = node;
        this.ctx = ctx;
        this.xmlBuilder = ctx.xmlBuilder;
        this.order = order;
    }

    public XMLBuilder getXmlBuilder() {
        return xmlBuilder;
    }

    public void setXmlBuilder(XMLBuilder xmlBuilder) {
        this.xmlBuilder = xmlBuilder;
    }

    public FormattingOrder getOrder() {
        return order;
    }

    public void setOrder(FormattingOrder order) {
        this.order = order;
    }

    abstract public void doFormatting();
}
