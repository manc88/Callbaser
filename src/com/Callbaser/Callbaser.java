package com.Callbaser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;

import javax.print.PrintService;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Callbaser {

    final static Logger log = Logger.getLogger(Callbaser.class.getName());

    static{
        final LogManager logManager = LogManager.getLogManager();

        try (final InputStream is = new FileInputStream("logging.conf")) {
            logManager.readConfiguration(is);
        }catch (Exception e){
            log.log(Level.WARNING,"Error loading logging config",e);
        }

        try {
            Config.load();
        } catch (CallbaserConfigLoadException e) {
            log.log(Level.WARNING,"Parse error loading config",e);
        } catch (IOException e) {
            log.log(Level.WARNING,"File error loading config",e);
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException{

        WatchService watchService = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(Config.MonitoringFolder);

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE
                //StandardWatchEventKinds.ENTRY_DELETE,
                //StandardWatchEventKinds.ENTRY_MODIFY
        );
        log.log(Level.SEVERE, "Start");
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                log.fine("File detected: " + event.context().toString());
                printPDF(Config.MonitoringFolder + "\\" + event.context().toString());


            }
            key.reset();
        }

        log.log(Level.SEVERE, "Stoped");
    }

    public static boolean printPDF(String filePath){

        try {

            PrintService ps = PrintUtility.findPrintService(Config.PrinterName);
            File file = new File(filePath);
            PDDocument doc = PDDocument.load(file);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(ps);
            job.setPageable(new PDFPageable(doc));
            job.print();
            log.fine("File printed : " + ps.getName());

        } catch (PrinterException e) {
            e.printStackTrace();
            log.log(Level.WARNING,"Print fail",e);
            return false;
        }catch (IOException e){
            log.log(Level.WARNING,"File fail",e);
            return false;
        }catch (Exception e){
            log.log(Level.WARNING,"Exception thrown",e);
            return false;
        }

        return true;

    }

}

