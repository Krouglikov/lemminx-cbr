package org.eclipse.lemminx.extensions.cbr.format.execution.dita;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.OverrideFormat;
import org.eclipse.lemminx.logs.LogToFile;
import org.eclipse.lemminx.utils.XMLBuilder;

public class SpTestFormat extends OverrideFormat {

    @Override
    public void accept(DOMNode domNode, XMLBuilder xmlBuilder) {
        LogToFile.getInstance().info("SpTestFormat#accetpt() is called");
        xmlBuilder.addContent("XXX");
    }
}
