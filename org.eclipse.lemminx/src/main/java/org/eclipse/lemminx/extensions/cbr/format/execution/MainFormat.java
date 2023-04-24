package org.eclipse.lemminx.extensions.cbr.format.execution;

import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormatConfiguration;

public class MainFormat extends NodeFormat {

    private final NodeFormatConfiguration configuration;

    public MainFormat(NodeFormatConfiguration configuration) {
        super(configuration.getCtx().rangeDomDocument, configuration.getCtx(), FormattingOrder.BEFORE_HEAD);
        configuration.getCtx().nodeFormatConfiguration = configuration;
        this.configuration = configuration;
    }

    @Override
    public void doFormatting() {
        configuration.getSequenceFormatForNode(node)
                .doFormatting();
    }

}
