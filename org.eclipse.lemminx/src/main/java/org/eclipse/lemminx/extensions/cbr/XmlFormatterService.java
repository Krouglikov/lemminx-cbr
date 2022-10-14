package org.eclipse.lemminx.extensions.cbr;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.extensions.cbr.format.FormatConfiguration;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.MainFormat;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Реализует форматирование текстовых значений разбиением на строки не более наперед заданной длины
 */
public class XmlFormatterService {

    //region Fields and constants

    public static final int DEFAULT_MAX_LINE_LENGTH = 100;


    private static final Logger LOGGER = Logger.getLogger(XmlFormatterService.class.getName());
    private static final XmlFormatterService INSTANCE;
    private static int maxLineLength = DEFAULT_MAX_LINE_LENGTH;
    private boolean enabled = false;

    //endregion

    static {
        INSTANCE = new XmlFormatterService();
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
            XmlFormatterService.maxLineLength = maxLineLength;
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
        Context context = new Context(textDocument, range, sharedSettings, formatterParticipants);
        MainFormat
                .configure(FormatConfiguration.cbr())
                .withContext(context)
                .accept(context.rangeDomDocument, context.xmlBuilder);
        List<? extends TextEdit> textEdits;
        try {
            textEdits = context.getFormatTextEdit();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        return textEdits;
    }

}
