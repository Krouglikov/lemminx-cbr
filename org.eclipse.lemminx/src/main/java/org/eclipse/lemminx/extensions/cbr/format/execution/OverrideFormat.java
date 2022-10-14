package org.eclipse.lemminx.extensions.cbr.format.execution;

import org.eclipse.lemminx.extensions.cbr.format.ContextBoundFormat;
import org.eclipse.lemminx.extensions.cbr.format.Format;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class OverrideFormat extends ContextBoundFormat {

    private Class<? extends Format>[] overrides;

    @Override
    public Stream<Class<? extends Format>> overrides() {
        return Arrays.stream(overrides);
    }

    public OverrideFormat(Class<? extends Format>... overrides) {
        super(Priority.OVERRIDE);
        this.overrides = overrides;
    }

}
