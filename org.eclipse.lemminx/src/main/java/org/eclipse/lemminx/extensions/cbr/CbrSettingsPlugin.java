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
import org.eclipse.lemminx.logs.LogToFile;
import org.eclipse.lsp4j.InitializeParams;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static org.eclipse.lemminx.extensions.cbr.CbrXMLFormatterDocument.DEFAULT_MAX_LINE_LENGTH;

/**
 * Обслуживает настройку основного полезного класса {@link CbrXMLFormatterDocument}
 */
public class CbrSettingsPlugin implements IXMLExtension {

    @Override
    public void start(InitializeParams params, XMLExtensionsRegistry registry) {
        CbrXMLFormatterDocument.setXmlLanguageService((XMLLanguageService) registry);
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
        int maxLineLength = ofNullable(map.getAsJsonPrimitive("maxStringWidth")) // maxStingLength
                .map(JsonPrimitive::getAsInt).orElse(DEFAULT_MAX_LINE_LENGTH);
        CbrXMLFormatterDocument.setMaxLineLength(maxLineLength);

        JsonArray catalogs = map.getAsJsonArray("catalogs");
        CbrXMLFormatterDocument.setDtdCatalogs(
                StreamSupport.stream(catalogs.spliterator(), false)
                        .map(JsonElement::getAsString).toArray(String[]::new)
        );
        overrideDitaBlockElementsFromSettingsJsonExtensionConfigurationFile(map);
        LogToFile.getInstance().info("CBR settings have been loaded from settings.json");

    }

    /**
     * Overrides the List of Block Elements if definition of xml.format.blockElements is present in settings
     * String array in settings.json
     *
     * @param jsonObject contains settings.json
     */
    private void overrideDitaBlockElementsFromSettingsJsonExtensionConfigurationFile(@Nonnull JsonObject jsonObject) {
        ofNullable(jsonObject.getAsJsonObject("format"))
                .flatMap(format -> ofNullable(format.getAsJsonArray("blockElements")))
                .map(object -> StreamSupport.stream(object.spliterator(), false)
                        .map(JsonElement::getAsString).collect(Collectors.toList()))
                .ifPresent(Predicates::setDitaBlockElements);
    }
}

