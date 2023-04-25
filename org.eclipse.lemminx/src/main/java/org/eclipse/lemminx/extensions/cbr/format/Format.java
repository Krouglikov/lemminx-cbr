package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.stream.Stream;

public abstract class Format {
    protected enum Priority {
        // Основной формат
        BASE,
        // Формат на замену основному
        OVERRIDE
    }

    protected Priority priority;
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


    public Stream<Class<? extends Format>> overrides() {
        return Stream.empty();
    }

    public Priority priority() {
        return priority;
    }

    abstract public void doFormatting();
}
