package org.eclipse.lemminx.extensions.cbr.format.library;

import org.eclipse.lemminx.extensions.cbr.format.Format;
import org.eclipse.lemminx.extensions.cbr.format.FormatSequence;
import org.eclipse.lemminx.extensions.cbr.utils.LogToFile;

public class MainFormat extends Format {

    private final FormatSequence formatSequence;

    public MainFormat(Context ctx) {
        super(ctx.rangeDomDocument, ctx, FormattingOrder.BEFORE_HEAD);
        formatSequence = new FormatSequence(ctx);
        ctx.formatSequence = formatSequence;
    }

    @Override
    public void doFormatting() {
        LogToFile.getInstance().info("doFormatting() formatSequence.getFormatListForNode(" +
                node.getNodeName() + ")");
        formatSequence.getFormatListForNode(node)
                .doFormatting();
        LogToFile.getInstance().info("FINISHED: doFormatting() formatSequence.getFormatListForNode(" +
                node.getNodeName() + ")");

    }

}
