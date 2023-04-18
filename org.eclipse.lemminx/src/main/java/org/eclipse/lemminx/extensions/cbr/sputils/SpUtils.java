package org.eclipse.lemminx.extensions.cbr.sputils;

import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.extensions.cbr.XmlFormatterService;
import org.eclipse.lemminx.extensions.contentmodel.settings.ContentModelSettings;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.extensions.contentmodel.uriresolver.XMLCatalogResolverExtension;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.eclipse.lemminx.utils.LogToFile;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class SpUtils {
    private static final Logger log = LogToFile.getInstance();

    /**
     * Provides validation of an XML document using DTD schema
     *
     * @param document XML document to be validated
     */

    public static boolean checkXmlValidWithDtdForFormatting(TextDocument document) {
        log.info("Starting validation");
        XMLLanguageService xmlLanguageService = XmlFormatterService.getXmlLanguageService() != null ?
                XmlFormatterService.getXmlLanguageService() : new XMLLanguageService();
        URIResolverExtensionManager manager = new URIResolverExtensionManager();

        String dtdCatalog = (XmlFormatterService.getDtdCatalogs() != null
                && !XmlFormatterService.getDtdCatalogs().isEmpty()) ?
                XmlFormatterService.getDtdCatalogs().get(0).toString() : "";
        XMLCatalogResolverExtension catalogResolverExtension = new XMLCatalogResolverExtension();
        catalogResolverExtension.setCatalogs(new String[]{dtdCatalog});
        manager.registerResolver(catalogResolverExtension);

        DOMDocument xmlDocument = DOMParser.getInstance().parse(document.getText(),
                document.getUri(), manager);
        xmlLanguageService.setDocumentProvider(uri -> xmlDocument);

        ContentModelSettings settings = new ContentModelSettings();
        settings.setUseCache(false);
        XMLValidationSettings problems = new XMLValidationSettings();
        settings.setValidation(problems);

        settings.getValidation().setResolveExternalEntities(true); // Important setting!

        List<Diagnostic> actual = xmlLanguageService.doDiagnostics(xmlDocument, settings.getValidation(),
                Collections.emptyMap(), () -> {
                });

        int[] severeMessagesCount = new int[1];
        StringBuilder sb = new StringBuilder("\nDiagnostics:\n");
        actual.forEach(d -> {
                    sb.append(d.getSeverity().toString())
                            .append(": ")
                            .append(d.getMessage());
                    severeMessagesCount[0] += d.getSeverity().equals(DiagnosticSeverity.Error) ? 1 : 0;
                }
        );

        if (severeMessagesCount[0] == 0) {
            log.info("\nValidation successful\n");
            return true;
        } else {
            log.info(sb + "\nValidation failed\n");
            return false;
        }
    }
}
