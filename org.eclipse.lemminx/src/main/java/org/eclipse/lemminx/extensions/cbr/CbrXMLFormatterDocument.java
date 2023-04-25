package org.eclipse.lemminx.extensions.cbr;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.MainFormat;
import org.eclipse.lemminx.extensions.cbr.utils.DitaValidator;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.extensions.cbr.utils.LogToFile;
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
    private final static String FORMATTING_DISABLED_BECAUSE_OF_ERRORS =
            "Форматирование не может быть выполнено - документ содержит ошибки";

    public static final int DEFAULT_MAX_LINE_LENGTH = 101;

    private static final Logger log = LogToFile.getInstance();
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
            Collection<IFormatterParticipant> formatterParticipants) {
        log.info("CbrXMLFormatterDocument#format() is invoked");
        Context context = new Context(textDocument, range, sharedSettings, formatterParticipants);

        if (!DitaValidator.checkXmlValidWithDtdBeforeFormatting(textDocument)) {
            sendValidationFailedNotification();
            return Collections.emptyList();
        }

        new MainFormat(context).doFormatting();

        List<? extends TextEdit> textEdits;
        try {
            textEdits = context.getFormatTextEdit();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        return textEdits;
    }

    private static void sendValidationFailedNotification() {
        if (CbrXMLFormatterDocument.getXmlLanguageService() != null &&
                CbrXMLFormatterDocument.getXmlLanguageService().getNotificationService() != null) {
            CbrXMLFormatterDocument.getXmlLanguageService().getNotificationService()
                    .sendNotification(FORMATTING_DISABLED_BECAUSE_OF_ERRORS, MessageType.Info);
        }
    }

}
