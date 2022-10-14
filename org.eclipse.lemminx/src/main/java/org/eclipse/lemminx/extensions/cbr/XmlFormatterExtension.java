package org.eclipse.lemminx.extensions.cbr;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.save.ISaveContext;
import org.eclipse.lsp4j.InitializeParams;

import static java.util.Optional.ofNullable;
import static org.eclipse.lemminx.extensions.cbr.XmlFormatterService.DEFAULT_MAX_LINE_LENGTH;

/**
 * Обслуживает настройку основного полезного класса {@link XmlFormatterService}
 */
public class XmlFormatterExtension implements IXMLExtension {

    @Override
    public void start(InitializeParams params, XMLExtensionsRegistry registry) {
        registry.registerComponent("CbrXmlFormatter");
    }

    @Override
    public void stop(XMLExtensionsRegistry registry) {
    }

    @Override
    public void doSave(ISaveContext context) {
        ofNullable(context)
                .filter(ctx -> ctx.getType() == ISaveContext.SaveContextType.SETTINGS)
                .map(ISaveContext::getSettings)
                .ifPresent(this::readSettings);
    }

    private void readSettings(Object settings) {
        if (settings instanceof JsonObject) {
            readSettings((JsonObject) settings);
        }
    }

    private void readSettings(JsonObject map) {
        int maxStringWidth = ofNullable(map.getAsJsonPrimitive("maxStringWidth"))
                .map(JsonPrimitive::getAsInt)
                .orElse(DEFAULT_MAX_LINE_LENGTH);
        XmlFormatterService.setMaxLineLength(maxStringWidth);
    }

}
