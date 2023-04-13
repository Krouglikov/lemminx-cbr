package org.eclipse.lemminx.extensions.cbr;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.save.ISaveContext;
import org.eclipse.lemminx.utils.LogToFile;
import org.eclipse.lsp4j.InitializeParams;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static org.eclipse.lemminx.extensions.cbr.XmlFormatterService.DEFAULT_MAX_LINE_LENGTH;
import static org.eclipse.lemminx.utils.LogToFile.GAP;

/**
 * Обслуживает настройку основного полезного класса {@link XmlFormatterService}
 */
public class XmlFormatterExtension implements IXMLExtension {


    @Override
    public void start(InitializeParams params, XMLExtensionsRegistry registry) {
        XmlFormatterService.setXmlLanguageService((XMLLanguageService)registry);
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
        if (map == null) {
            LogToFile.getInstance().info(GAP + "readSettings(JsonObject map) started but map == null");
            return;
        }
        LogToFile.getInstance().info(GAP + "readSettings(JsonObject map) started");
        int maxStringWidth = ofNullable(map.getAsJsonPrimitive("maxStringWidth"))
                .map(JsonPrimitive::getAsInt)
                .orElse(DEFAULT_MAX_LINE_LENGTH);
        XmlFormatterService.setMaxLineLength(maxStringWidth);

        JsonArray catalogs = map.getAsJsonArray("catalogs");
        Stream<Path> replacement =
                StreamSupport.stream(catalogs.spliterator(), false)
                        .map(x -> Paths.get(x.getAsString()));

        XmlFormatterService.getDtdCatalogs().clear();
        XmlFormatterService.getDtdCatalogs().addAll(replacement.collect(Collectors.toList()));
    }

}
