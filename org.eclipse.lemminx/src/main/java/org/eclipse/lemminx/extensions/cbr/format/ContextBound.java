package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.extensions.cbr.format.execution.Context;

public class ContextBound implements WithContext {

    protected Context ctx;

    public ContextBound() {
    }

    public ContextBound(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public Context getContext() {
        return ctx;
    }

    @Override
    public ContextBound withContext(Context c) {
        ctx = c;
        return this;
    }
}
