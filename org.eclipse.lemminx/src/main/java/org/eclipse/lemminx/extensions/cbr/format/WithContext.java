package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.extensions.cbr.format.execution.Context;

interface WithContext {

    Context getContext();

    WithContext withContext(Context c);

}
