package org.eclipse.lemminx.extensions.cbr;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.extensions.cbr.format.FormatConfiguration;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.MainFormat;
import org.eclipse.lemminx.extensions.cbr.sputils.SpUtils;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.logs.LogToFile;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;

import java.util.*;
import java.util.logging.Logger;

/**
 * Replaces the original XMLFormatterDocument with Cbr-featured formatting implementation.
 * Реализует форматирование текстовых значений разбиением на строки не более наперед заданной длины - just TODO
 */
public class CbrXMLFormatterDocument {

    //region Fields and constants
    private final static String FORMATTING_DISABLED_DUE_TO_ERRORS =
            "Форматирование не может быть выполнено - документ содержит ошибки";

    public static final int DEFAULT_MAX_LINE_LENGTH = 101;

    private static final Logger log = LogToFile.getInstance();
    private static final Logger LOGGER = Logger.getLogger(CbrXMLFormatterDocument.class.getName());
    private static final CbrXMLFormatterDocument INSTANCE;
    private static int maxLineLength = DEFAULT_MAX_LINE_LENGTH;

    private static String[] dtdCatalogs;
    private static XMLLanguageService xmlLanguageService;
    private boolean enabled = false;
    //endregion

    public static XMLLanguageService getXmlLanguageService() {
        return xmlLanguageService;
    }

    public static void setXmlLanguageService(XMLLanguageService xmlLanguageService) {
        CbrXMLFormatterDocument.xmlLanguageService = xmlLanguageService;
    }

    public static void setDtdCatalogs(String[] dtdCatalogs) {
        CbrXMLFormatterDocument.dtdCatalogs = dtdCatalogs;
    }

    public static String[] getDtdCatalogs() {
        return dtdCatalogs;
    }

    static {
        INSTANCE = new CbrXMLFormatterDocument();
        try {
            Properties properties = System.getProperties();
            String stringValue = (String) properties.getOrDefault("cbr.formatter.enabled", "true");
            INSTANCE.enabled = Boolean.parseBoolean(stringValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean enabled() {
        return INSTANCE.enabled;
    }

    public static void setMaxLineLength(int maxLineLength) {
        if (maxLineLength > 0) {
            CbrXMLFormatterDocument.maxLineLength = maxLineLength;
        }
    }

    public static int getMaxLineLength() {
        return maxLineLength;
    }

    public static List<? extends TextEdit> format(
            TextDocument textDocument,
            Range range,
            SharedSettings sharedSettings,
            Collection<IFormatterParticipant> formatterParticipants
    ) {
        log.info("org.eclipse.lemminx.extensions.cbr.XmlFormatterService#format() is invoked");
        if (!SpUtils.checkXmlValidWithDtdBeforeFormatting(textDocument)) {
            CbrXMLFormatterDocument.getXmlLanguageService().getNotificationService()
                    .sendNotification(FORMATTING_DISABLED_DUE_TO_ERRORS, MessageType.Info);
            return Collections.emptyList();
        }
        Context context = new Context(textDocument, range, sharedSettings, formatterParticipants);
        MainFormat mainFormat = MainFormat.configure(FormatConfiguration.cbr());
                mainFormat.withContext(context);
                mainFormat.accept(context.rangeDomDocument, context.xmlBuilder);

        List<? extends TextEdit> textEdits;
        try {
            textEdits = context.getFormatTextEdit();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        return textEdits;
    }

}
