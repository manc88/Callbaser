package com.Callbaser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Clog {

    private final static Logger log = Logger.getLogger(Callbaser.class.getName());

    static {
        final LogManager logManager = LogManager.getLogManager();
        try (final InputStream is = new FileInputStream("logging.conf")) {
            logManager.readConfiguration(is);
        } catch (Exception e) {
            log.log(Level.WARNING, "Error loading logging config", e);
        }
    }

    static void info(String msg) {
        log.log(Level.INFO, msg);
    }

    static void warn(String msg, Exception e) {
        log.log(Level.WARNING, "Exception occur: " + msg, e);
    }

}
