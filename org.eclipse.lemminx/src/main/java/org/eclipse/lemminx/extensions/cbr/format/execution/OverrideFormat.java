package org.eclipse.lemminx.extensions.cbr.format.execution;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.NodeFormat;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class OverrideFormat extends NodeFormat {

    private Class<? extends NodeFormat>[] overrides;

    public OverrideFormat(DOMNode node, Context ctx, FormattingOrder order) {
        super(node, ctx, order);
    }

    @Override
    public Stream<Class<? extends NodeFormat>> overrides() {
        return Arrays.stream(overrides);
    }

//    public OverrideFormat(Class<? extends ContextBoundFormat>... overrides) {
//        super(Priority.OVERRIDE);
//        this.overrides = overrides;
//    }
}
