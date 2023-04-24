package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public abstract class ContextBoundFormat extends ContextBound implements Format {

    private final Priority priority;

    public ContextBoundFormat() {
        this.priority = Priority.BASE;
    }

    @Override
    public Stream<Class<? extends Format>> overrides() {
        return Stream.empty();
    }

    public ContextBoundFormat(Priority priority) {
        this.priority = priority;
    }

    @Override
    public Priority priority() {
        return priority;
    }

    @Override
    public Format withContext(Object context) {
        if (Context.class.isAssignableFrom(context.getClass())) {
            this.ctx = (Context) context;
        }
        return this;
    }

}
