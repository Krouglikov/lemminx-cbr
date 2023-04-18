package org.eclipse.lemminx.extensions.cbr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.extensions.cbr.format.Predicates;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.save.ISaveContext;
import org.eclipse.lsp4j.InitializeParams;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static org.eclipse.lemminx.extensions.cbr.XmlFormatterService.DEFAULT_MAX_LINE_LENGTH;

/**
 * Обслуживает настройку основного полезного класса {@link XmlFormatterService}
 */
public class XmlFormatterExtension implements IXMLExtension {

    @Override
    public void start(InitializeParams params, XMLExtensionsRegistry registry) {
        XmlFormatterService.setXmlLanguageService((XMLLanguageService) registry);
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
        if (map == null) return;
        int maxStringWidth = ofNullable(map.getAsJsonPrimitive("maxStringWidth"))
                .map(JsonPrimitive::getAsInt).orElse(DEFAULT_MAX_LINE_LENGTH);
        XmlFormatterService.setMaxLineLength(maxStringWidth);

        JsonArray catalogs = map.getAsJsonArray("catalogs");
        Stream<Path> replacement =
                StreamSupport.stream(catalogs.spliterator(), false).map(x -> Paths.get(x.getAsString()));

        XmlFormatterService.getDtdCatalogs().clear();
        XmlFormatterService.getDtdCatalogs().addAll(replacement.collect(Collectors.toList()));

        overrideDitaBlockElementsFromSettingsJsonExtensionConfigurationFile(map);
    }

    /**
     * Overrides the List of Block Elements if definition of xml.format.blockElements is present in settings
     * String array in settings.json
     *
     * @param jsonObject refers to settings.json content
     */
    private void overrideDitaBlockElementsFromSettingsJsonExtensionConfigurationFile(@Nonnull JsonObject jsonObject) {
        ofNullable(jsonObject.getAsJsonObject("format"))
                .flatMap(format -> ofNullable(format.getAsJsonArray("blockElements")))
                .map(object -> StreamSupport.stream(object.spliterator(), false)
                        .map(JsonElement::getAsString).collect(Collectors.toList()))
                .ifPresent(Predicates::setDitaBlockElements);
    }
}

