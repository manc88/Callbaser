package com.Callbaser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class PrintTask implements Runnable {

    private File file;
    private LocalDateTime freshTill;
    private boolean cleanup;

    PrintTask(File f, boolean c) {
        file = f;
        cleanup = c;
        freshTill = LocalDateTime.now().plus(Duration.of(1, ChronoUnit.MINUTES));
    }

    @Override
    public void run() {

        Utils.sleep(1000);
        //w8 for obj access
        while (!file.canWrite() && freshTill.isAfter(LocalDateTime.now())) {
            Utils.sleep(1000);
        }
        if (file.canWrite()) {

            try (PDDocument doc = PDDocument.load(file)) {

                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintService(Config.ps);
                //job.setPageable(new PDFPageable(doc));
                //job.print();
                Clog.info("File " +file.getName() +" printed on service: " + Config.ps.getName());
                System.out.println("File " +file.getName() + " printed on service: " + Config.ps.getName());
                job = null;

            } catch (PrinterException e) {
                e.printStackTrace();
                Clog.warn("Print failed", e);
            } catch (IOException e) {
                Clog.warn("PDD document failed to load file:" + file.getName(), e);
            } catch (Exception e) {
                Clog.warn("Unknown exception", e);
            }

        } else {
            Clog.warn("No access to file:" + file.getName(), new Exception());
        }

        Utils.sleep(1000);
        if(cleanup){
            cleanup(file);
        }

    }

    private void cleanup(File file) {

        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            Clog.warn("Clean up error:" + file.getName(), e);
        }

    }

}
