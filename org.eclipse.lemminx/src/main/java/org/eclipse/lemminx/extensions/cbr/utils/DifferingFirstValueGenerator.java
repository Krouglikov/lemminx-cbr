package org.eclipse.lemminx.extensions.cbr.utils;

import java.util.function.Supplier;

public class DifferingFirstValueGenerator<X> implements Supplier<X> {
    private final Supplier<X> first;
    private final Supplier<X> other;
    private boolean firstOut = false;

    public DifferingFirstValueGenerator(X first, X other) {
        this.first = () -> first;
        this.other = () -> other;
    }

    public DifferingFirstValueGenerator(Supplier<X> first, Supplier<X> other) {
        this.first = first;
        this.other = other;
    }

    @Override
    public X get() {
        if (!firstOut) {
            firstOut = true;
            return first.get();
        } else {
            return other.get();
        }
    }
}
