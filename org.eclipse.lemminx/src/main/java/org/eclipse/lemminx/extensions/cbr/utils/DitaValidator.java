package org.eclipse.lemminx.extensions.cbr.utils;

import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.extensions.cbr.CbrXMLFormatterDocument;
import org.eclipse.lemminx.extensions.contentmodel.settings.ContentModelSettings;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.extensions.contentmodel.uriresolver.XMLCatalogResolverExtension;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class DitaValidator {
    private static final Logger log = LogToFile.getInstance();
    private static final Logger LOGGER = Logger.getLogger(DitaValidator.class.getName());

    /**
     * Provides validation of an XML document using DTD schema
     *
     * @param document XML document to be validated
     */

    public static boolean checkXmlValidWithDtdBeforeFormatting(TextDocument document) {
        log.info("Starting validation");
        LOGGER.info("Starting validation - logging to the Lemminx default logger");
        XMLLanguageService xmlLanguageService = CbrXMLFormatterDocument.getXmlLanguageService() != null ?
                CbrXMLFormatterDocument.getXmlLanguageService() : new XMLLanguageService();
        XMLCatalogResolverExtension catalogResolverExtension = new XMLCatalogResolverExtension();
        catalogResolverExtension.setCatalogs(CbrXMLFormatterDocument.getDtdCatalogs());
        URIResolverExtensionManager manager = new URIResolverExtensionManager();
        manager.registerResolver(catalogResolverExtension);

        DOMDocument xmlDocument = DOMParser.getInstance().parse(document.getText(), document.getUri(), manager);
        xmlLanguageService.setDocumentProvider(uri -> xmlDocument);

        ContentModelSettings settings = new ContentModelSettings();
        settings.setUseCache(false);
        settings.setValidation(new XMLValidationSettings());
        settings.getValidation().setResolveExternalEntities(true); // The setting is important!

        return isNoSevereDiagnostics(xmlLanguageService.doDiagnostics(xmlDocument, settings.getValidation(),
                Collections.emptyMap(), () -> {
                }));
    }

    private static boolean isNoSevereDiagnostics(List<Diagnostic> actual) {
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
            log.info(sb + "\nValidation failed because of severe diagnostics\n");
            return false;
        }
    }
}
