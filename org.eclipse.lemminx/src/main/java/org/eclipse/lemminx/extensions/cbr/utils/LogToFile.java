package org.eclipse.lemminx.extensions.cbr.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public final class LogToFile {
    public static final String GAP = "\t\t\t\t\t\t\t\t\t\t\t\t\t";

    private static final String LOG_PATH = "c:\\Java\\Logs\\";
    private static java.util.logging.Logger instance;

    private LogToFile() {
    }

    public static java.util.logging.Logger getFileLoggerInstance() {
        if (instance == null) {
            instance = java.util.logging.Logger.getLogger(LogToFile.class.getName());
            instance.info(GAP + "Logger: The instance is assigned");
            FileHandler fh = null;
            try {
                fh = new FileHandler(LOG_PATH + "dita-lemminx-cbr-log.txt", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert fh != null;
            fh.setFormatter(new SimpleFormatter());
            instance.addHandler(fh);
//        instance.setUseParentHandlers(false);
        }
        return instance;
    }
}