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

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class SpUtils {
    private static Logger log = LogToFile.getInstance();

    /**
     * Provides validation of an XML document using DTD schema
     *
     * @param document XML document to be validated
     */

    public static boolean checkXmlValidWithDtd(TextDocument document) {
        log.info("SpUtils#checkXmlValidWithDtd() started");
        XMLLanguageService xmlLanguageService = XmlFormatterService.getXmlLanguageService() != null ?
                XmlFormatterService.getXmlLanguageService() : new XMLLanguageService();
        URIResolverExtensionManager manager = new URIResolverExtensionManager();

        String[] catalogs = {XmlFormatterService.getDtdCatalogs().get(0).toString()};
        XMLCatalogResolverExtension catalogResolverExtension = new XMLCatalogResolverExtension();
        catalogResolverExtension.setCatalogs(catalogs);
        manager.registerResolver(catalogResolverExtension);

        DOMDocument xmlDocument = DOMParser.getInstance().parse(document.getText(),
                document.getUri(), manager);
        xmlLanguageService.setDocumentProvider(uri -> xmlDocument);

        ContentModelSettings settings = new ContentModelSettings();
        settings.setUseCache(false);
        XMLValidationSettings problems = new XMLValidationSettings();
        settings.setValidation(problems);

        log.info("Ready for calling xmlLanguageService.doDiagnostics() ");

        settings.getValidation().setResolveExternalEntities(true); // Important setting!

        List<Diagnostic> actual = xmlLanguageService.doDiagnostics(xmlDocument, settings.getValidation(),
                Collections.emptyMap(), () -> {
                });

        // TODO Put the analysis of the severity of the errors here...

        if (!actual.isEmpty()) {
            log.info("SpUtils#checkXmlValidWithDtd() finished and returned false");
            return false;
        } else {
            log.info("SpUtils#checkXmlValidWithDtd() finished and returned true");
            return true;
        }
    }
}
