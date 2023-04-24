package org.eclipse.lemminx.extensions.cbr.format.execution;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.FormatConfiguration;
import org.eclipse.lemminx.utils.XMLBuilder;

public class MainFormat extends ContextBoundFormat {

    private final FormatConfiguration configuration;

    private MainFormat(FormatConfiguration configuration) {
        this.configuration = configuration;
    }

    public static MainFormat configure(FormatConfiguration configuration) {
        return new MainFormat(configuration);
    }

    @Override
    public MainFormat withContext(Context ctx) {
        ctx.formatConfiguration = this.configuration;
        configuration.setCtx(ctx);
        this.ctx = ctx;
        return this;
    }

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        configuration.getSequenceFormatForNode(domNode)
                .doFormatting();
    }

}
